/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */

package fr.gouv.recherche.scanr.workflow.full;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.Striped;
import fr.gouv.recherche.scanr.companies.workflow.service.QueueService;
import fr.gouv.recherche.scanr.db.model.Person;
import fr.gouv.recherche.scanr.db.model.PersonRelation;
import fr.gouv.recherche.scanr.db.model.Project;
import fr.gouv.recherche.scanr.db.model.Structure;
import fr.gouv.recherche.scanr.db.model.full.*;
import fr.gouv.recherche.scanr.db.model.publication.Publication;
import fr.gouv.recherche.scanr.db.model.publication.PublicationAuthorRelation;
import fr.gouv.recherche.scanr.db.repository.*;
import fr.gouv.recherche.scanr.workflow.search.IndexPersonProcess;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.locks.Lock;

@Service
public class FullPersonService {

    private static final Logger log = LoggerFactory.getLogger(FullPersonService.class);

    private Striped<Lock> lock = Striped.lock(64);

    @Autowired(required = false)
    private List<FullPersonProvider> providers = Lists.newArrayList();

    @Autowired
    private FullPersonRepository repository;
    @Autowired
    private PersonRepository personRepository;

    @Autowired
    FullStructureService fullStructureService;
    @Autowired
    private FullStructureRepository fullStructureRepository;
    @Autowired
    private StructureRepository structureRepository;

    @Autowired
    private FullProjectRepository fullProjectRepository;
    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private FullPublicationRepository fullPublicationRepository;
    @Autowired
    private PublicationRepository publicationRepository;

    @Autowired
    private QueueService service;

    private Map<FullPersonField, FullPersonProvider> providerIdx = new EnumMap<>(FullPersonField.class);
    private Multimap<FullPersonField, FullPersonField> transitiveDependencies = HashMultimap.create();
    private Multimap<FullPersonField, FullPersonField> directDependencies = HashMultimap.create();

    @PostConstruct
    protected void init() {
        // Compute direct dependencies graph & provider index
        for (FullPersonProvider<?> provider : providers) {
            FullPersonField field = provider.getField();
            if (field != null) {
                providerIdx.put(provider.getField(), provider);

                log.info("Registering provider {} for field {}", provider.getClass().getName(), provider.getField());
                Set<FullPersonField> dependencies = provider.getDependencies();
                if (dependencies != null) {
                    for (FullPersonField dependencyField : provider.getDependencies()) {
                        directDependencies.put(dependencyField, provider.getField());
                    }
                }
            }
        }

        computeTransitiveDependencies();
    }

    protected void computeTransitiveDependencies() {
        // Compute the transitive dependencies
        for (FullPersonField field : directDependencies.keySet()) {
            Set<FullPersonField> deps = Sets.newHashSet();
            fillDependencies(field, directDependencies, deps);
            transitiveDependencies.putAll(field, deps);
        }
    }

    private static void fillDependencies(FullPersonField field, Multimap<FullPersonField, FullPersonField> dep,
                                         Set<FullPersonField> result) {
        if (!dep.containsKey(field)) {
            return;
        }
        for (FullPersonField depField : dep.get(field)) {
            result.add(depField);
            fillDependencies(depField, dep, result);
        }
    }

    /**
     * Create a transaction on an identifier. When you are inside the context of a TX you are sure that you are alone
     * working on this structure. Also, the save will throw a full person updated message.
     * <p/>
     * <pre>
     *    try(FullPersonTransaction tx = service.tx("FR-123456789", false)) {
     *        tx.setField(...);
     *        tx.save();
     *    }
     * </pre>
     *
     * @param id The person identifier
     * @param failOnAbsent If true,
     * @return The TX object
     */
    public FullPersonTransaction tx(String id, boolean failOnAbsent) {
        if (StringUtils.isEmpty(id)) {
            throw new IllegalArgumentException("Id can't null or empty.");
        }

        try {
            Person person = personRepository.findOne(id);
            if (person == null) {
                if (failOnAbsent) {
                    throw new NoSuchElementException("Unknown person " + id);
                } else {
                    return null;
                }
            }
            log.debug("Creating full person " + id);

            // Create the new full person
            FullPerson newFullPerson = new FullPerson(id);
            BeanUtils.copyProperties(person, newFullPerson);

            Set<FullPersonField> changedFields = cleanDirtyFields(newFullPerson, Sets.newHashSet(FullPersonField.values()));
            FullPersonTransaction transaction = new FullPersonTransaction(this, newFullPerson, true);
            return transaction;
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * Utility method that allows a user to refresh a field easily
     *
     * @param id    The identifier
     * @param field The field to update
     */
    public void refresh(String id, FullPersonField field) {
        try (FullPersonTransaction tx = tx(id, true)) {
            tx.refresh(field);
            tx.save(false, false);
        }
    }

    /**
     * Utility method that allows a user to refresh a field easily
     * Does not throw IAE if the person doesnt exist
     *
     * @param id    The identifier
     * @param field The field to update
     */
    public void refreshIfExists(String id, FullPersonField field) {
        try (FullPersonTransaction tx = tx(id, false)) {
            if (tx == null) {
                return;
            }
            tx.refresh(field);
            tx.save(false, false);
        }
    }

    /**
     * Shortcut of #getAndUpdateField with create = true
     *
     * @param id    The identifier
     * @param field The field to update
     * @param data  The data to put
     * @param <E>   The data type
     */
    public <E> void getAndUpdateField(String id, FullPersonField field, E data) {
        try (FullPersonTransaction tx = tx(id, true)) {
            tx.setField(field, data);
            tx.save(false, false);
        }
    }

    /**
     * Utility method to ensure that the id is created in the base.
     *
     * @param id The person identifier
     */
    public void ensureCreated(String id) {
        try (FullPersonTransaction tx = tx(id, true)) {
            tx.save(false, false);
        }
    }

    /**
     * Utility method to ensure that the id is created in the base.
     *
     * @param id The person identifier
     */
    public void delayedRefresh(String id, FullPersonField... fields) {
        try {
            repository.addDelayedFieldToRefresh(id, fields);
        } finally {
        }
    }

    /**
     * Utility method to ensure that the id is created in the base.
     *
     * @param id The person identifier
     */
    public void notifyIndexed(String id) {
        try {
            repository.notifyIndexed(id);
        } finally {
        }
    }

    protected void delete(FullPersonTransaction tx, boolean forcePropagation) {
        log.debug("[{}] Removing full person ", tx.getData().getId());
        repository.delete(tx.getData().getId());

        // propagate notification
        if (forcePropagation) {
            // Notify the index that the structure needs to be updated
            service.push(tx.getData().getId(), IndexPersonProcess.QUEUE);
        }
    }

    protected void save(FullPersonTransaction tx, boolean forcePropagation, boolean forIndex) {
        repository.save(tx.getData());
        log.debug("[{}] Saving", tx.getData().getId());
    }

    /**
     * Set the field from a full person and update all the fields based on their providers.
     *
     * @param person The full person object
     * @param field     The field to update
     * @param data      The data to put
     * @param <E>       The data type
     * @return The list of updated fields (the given field is included)
     */
    protected <E> Set<FullPersonField> setField(FullPerson person, FullPersonField field, E data) {
        field.setter().accept(person, data);
        Set<FullPersonField> changedFields = cleanDirtyFields(person, Sets.newHashSet(directDependencies.get(field)));
        changedFields.add(field);
        return changedFields;
    }

    /**
     * Check of clean fields that are dirty. This will call for providers to create their respective entries.
     * <p/>
     * This must guarantee the following restrictions:
     * <li>A provider is called if it agreed on the content (via #canProvide)</li>
     * <li>A provider is called AFTER all dependent dirty fields have been resolved</li>
     * <li>A field is marked dirty if a dependent provider has provided a new value that was not equal (with #equals)
     * to the previous value</li>
     * <li>A field is reset to null if the provider cannot provide, and must propagate its new null value (if necessary)</li>
     *
     * @param person   The full person object
     * @param dirtyFields The dirty fields to check or clean
     * @return The list of affected fields
     */
    protected Set<FullPersonField> cleanDirtyFields(FullPerson person, Set<FullPersonField> dirtyFields) {
        Set<FullPersonField> changedFields = Sets.newHashSet();
        if (dirtyFields.isEmpty())
            return changedFields;

        Multimap<FullPersonField, FullPersonField> reverseDependencies = HashMultimap.create();

        // While we still have fields to clean
        do {
            // Compute the reverse dependencies graph
            reverseDependencies.clear();
            for (FullPersonField dirtyField : dirtyFields) {
                transitiveDependencies.get(dirtyField).stream()
                        .filter(dirtyFields::contains)
                        .forEach(dirtyDep -> reverseDependencies.put(dirtyDep, dirtyField));
            }

            // Check for any field that has no dependency
            FullPersonField dirtyField = dirtyFields.stream().filter(it -> reverseDependencies.get(it).isEmpty()).findAny().get();

            // Get the provider
            FullPersonProvider provider = providerIdx.get(dirtyField);
            if (provider == null) {
                throw new IllegalStateException("No provider were found for " + dirtyField);
            }

            // Try to set the field
            Object old = dirtyField.getter().apply(person);

            log.trace("[{}] Calling provider for field {}", person.getId(), dirtyField);
            Object newValue = null;
            // Update if necessary
            if (provider.canProvide(person)) {
                newValue = provider.computeField(person);
            }
            // If old and new are considered equal, there no need to propagate the changes
            if (old == null && newValue != null || old != null && !old.equals(newValue)) {
                log.trace("[{}] Field {} has changed, propagating to dependencies", person.getId(), dirtyField);
                dirtyField.setter().accept(person, newValue);
                changedFields.add(dirtyField);

                // Add the newly dirty fields
                dirtyFields.addAll(directDependencies.get(dirtyField));
            } else if (newValue == null) {
                log.trace("[{}] Field {} remains null", person.getId(), dirtyField);
            }

            // Now this is clean
            dirtyFields.remove(dirtyField);
        } while (!dirtyFields.isEmpty());
        return changedFields;
    }

    protected Map<FullPersonField, FullPersonProvider> getProviderIdx() {
        return providerIdx;
    }

    protected Multimap<FullPersonField, FullPersonField> getTransitiveDependencies() {
        return transitiveDependencies;
    }

    protected Multimap<FullPersonField, FullPersonField> getDirectDependencies() {
        return directDependencies;
    }

    public FullPersonRepository getRepository() {
        return repository;
    }
}
