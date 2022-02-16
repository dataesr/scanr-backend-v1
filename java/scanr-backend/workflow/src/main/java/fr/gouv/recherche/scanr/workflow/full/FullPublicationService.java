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
import fr.gouv.recherche.scanr.db.model.full.*;
import fr.gouv.recherche.scanr.db.model.publication.Publication;
import fr.gouv.recherche.scanr.db.repository.FullPublicationRepository;
import fr.gouv.recherche.scanr.db.repository.PublicationRepository;
import fr.gouv.recherche.scanr.workflow.search.IndexPublicationProcess;
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
public class FullPublicationService {

    private static final Logger log = LoggerFactory.getLogger(FullPublicationService.class);

    private Striped<Lock> lock = Striped.lock(64);

    @Autowired(required = false)
    private List<FullPublicationProvider> providers = Lists.newArrayList();

    @Autowired
    private FullPublicationRepository repository;
    @Autowired
    private PublicationRepository publicationRepository;

    @Autowired
    private FullProjectService fullProjectService;
    @Autowired
    private FullPersonService fullPersonService;
    @Autowired
    private FullStructureService fullStructureService;

    @Autowired
    private QueueService service;

    private Map<FullPublicationField, FullPublicationProvider> providerIdx = new EnumMap<>(FullPublicationField.class);
    private Multimap<FullPublicationField, FullPublicationField> transitiveDependencies = HashMultimap.create();
    private Multimap<FullPublicationField, FullPublicationField> directDependencies = HashMultimap.create();

    @PostConstruct
    protected void init() {
        // Compute direct dependencies graph & provider index
        for (FullPublicationProvider<?> provider : providers) {
            FullPublicationField field = provider.getField();
            if (field != null) {
                providerIdx.put(provider.getField(), provider);

                log.info("Registering provider {} for field {}", provider.getClass().getName(), provider.getField());
                Set<FullPublicationField> dependencies = provider.getDependencies();
                if (dependencies != null) {
                    for (FullPublicationField dependencyField : provider.getDependencies()) {
                        directDependencies.put(dependencyField, provider.getField());
                    }
                }
            }
        }

        computeTransitiveDependencies();
    }

    protected void computeTransitiveDependencies() {
        // Compute the transitive dependencies
        for (FullPublicationField field : directDependencies.keySet()) {
            Set<FullPublicationField> deps = Sets.newHashSet();
            fillDependencies(field, directDependencies, deps);
            transitiveDependencies.putAll(field, deps);
        }
    }

    private static void fillDependencies(FullPublicationField field, Multimap<FullPublicationField, FullPublicationField> dep,
                                         Set<FullPublicationField> result) {
        if (!dep.containsKey(field)) {
            return;
        }
        for (FullPublicationField depField : dep.get(field)) {
            result.add(depField);
            fillDependencies(depField, dep, result);
        }
    }

    /**
     * Create a transaction on an identifier. When you are inside the context of a TX you are sure that you are alone
     * working on this structure. Also, the save will throw a full publication updated message.
     * <p/>
     * <pre>
     *    try(FullPublicationTransaction tx = service.tx("FR-123456789", false)) {
     *        tx.setField(...);
     *        tx.save();
     *    }
     * </pre>
     *
     * @param id The publication identifier
     * @param failOnAbsent If true,
     * @return The TX object
     */
    public FullPublicationTransaction tx(String id, boolean failOnAbsent) {
        if (StringUtils.isEmpty(id)) {
            throw new IllegalArgumentException("Id can't null or empty.");
        }

        try {
            Publication publication = publicationRepository.findOne(id);
            if (publication == null) {
                if (failOnAbsent) {
                    throw new NoSuchElementException("Unknown publication " + id);
                } else {
                    return null;
                }
            }
            log.debug("Creating full publication " + id);

            // Create the new full publication
            FullPublication newFullPublication = new FullPublication(id);
            BeanUtils.copyProperties(publication, newFullPublication);

            Set<FullPublicationField> changedFields = cleanDirtyFields(newFullPublication, Sets.newHashSet(FullPublicationField.values()));
            FullPublicationTransaction transaction = new FullPublicationTransaction(this, newFullPublication,true);
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
    public void refresh(String id, FullPublicationField field) {
        try (FullPublicationTransaction tx = tx(id, true)) {
            tx.refresh(field);
            tx.save(false, false);
        }
    }

    /**
     * Utility method that allows a user to refresh a field easily
     * Does not throw IAE if the publication doesnt exist
     *
     * @param id    The identifier
     * @param field The field to update
     */
    public void refreshIfExists(String id, FullPublicationField field) {
        try (FullPublicationTransaction tx = tx(id, false)) {
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
    public <E> void getAndUpdateField(String id, FullPublicationField field, E data) {
        try (FullPublicationTransaction tx = tx(id, true)) {
            tx.setField(field, data);
            tx.save(false, false);
        }
    }

    /**
     * Utility method to ensure that the id is created in the base.
     *
     * @param id The publication identifier
     */
    public void ensureCreated(String id) {
        try (FullPublicationTransaction tx = tx(id, true)) {
            tx.save(false, false);
        }
    }

    /**
     * Utility method to ensure that the id is created in the base.
     *
     * @param id The publication identifier
     */
    public void delayedRefresh(String id, FullPublicationField... fields) {
        try {
            repository.addDelayedFieldToRefresh(id, fields);
        } finally {
        }
    }

    /**
     * Utility method to ensure that the id is created in the base.
     *
     * @param id The publication identifier
     */
    public void notifyIndexed(String id) {
        try {
            repository.notifyIndexed(id);
        } finally {
        }
    }

    protected void delete(FullPublicationTransaction tx, boolean forcePropagation) {
        log.debug("[{}] Removing full publication ", tx.getData().getId());
        repository.delete(tx.getData().getId());

        // propagate notification
        if (forcePropagation) {
            // Notify the index that the structure needs to be updated
            service.push(tx.getData().getId(), IndexPublicationProcess.QUEUE);
        }
    }

    protected void save(FullPublicationTransaction tx, boolean forcePropagation, boolean forIndex) {
        repository.save(tx.getData());
        log.debug("[{}] Saving", tx.getData().getId());
    }

    /**
     * Set the field from a full publication and update all the fields based on their providers.
     *
     * @param publication The full publication object
     * @param field     The field to update
     * @param data      The data to put
     * @param <E>       The data type
     * @return The list of updated fields (the given field is included)
     */
    protected <E> Set<FullPublicationField> setField(FullPublication publication, FullPublicationField field, E data) {
        field.setter().accept(publication, data);
        Set<FullPublicationField> changedFields = cleanDirtyFields(publication, Sets.newHashSet(directDependencies.get(field)));
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
     * @param publication   The full publication object
     * @param dirtyFields The dirty fields to check or clean
     * @return The list of affected fields
     */
    protected Set<FullPublicationField> cleanDirtyFields(FullPublication publication, Set<FullPublicationField> dirtyFields) {
        Set<FullPublicationField> changedFields = Sets.newHashSet();
        if (dirtyFields.isEmpty())
            return changedFields;

        Multimap<FullPublicationField, FullPublicationField> reverseDependencies = HashMultimap.create();

        // While we still have fields to clean
        do {
            // Compute the reverse dependencies graph
            reverseDependencies.clear();
            for (FullPublicationField dirtyField : dirtyFields) {
                transitiveDependencies.get(dirtyField).stream()
                        .filter(dirtyFields::contains)
                        .forEach(dirtyDep -> reverseDependencies.put(dirtyDep, dirtyField));
            }

            // Check for any field that has no dependency
            FullPublicationField dirtyField = dirtyFields.stream().filter(it -> reverseDependencies.get(it).isEmpty()).findAny().get();

            // Get the provider
            FullPublicationProvider provider = providerIdx.get(dirtyField);
            if (provider == null) {
                throw new IllegalStateException("No provider were found for " + dirtyField);
            }

            // Try to set the field
            Object old = dirtyField.getter().apply(publication);

            log.trace("[{}] Calling provider for field {}", publication.getId(), dirtyField);
            Object newValue = null;
            // Update if necessary
            if (provider.canProvide(publication)) {
                newValue = provider.computeField(publication);
            }
            // If old and new are considered equal, there no need to propagate the changes
            if (old == null && newValue != null || old != null && !old.equals(newValue)) {
                log.trace("[{}] Field {} has changed, propagating to dependencies", publication.getId(), dirtyField);
                dirtyField.setter().accept(publication, newValue);
                changedFields.add(dirtyField);

                // Add the newly dirty fields
                dirtyFields.addAll(directDependencies.get(dirtyField));
            } else if (newValue == null) {
                log.trace("[{}] Field {} remains null", publication.getId(), dirtyField);
            }

            // Now this is clean
            dirtyFields.remove(dirtyField);
        } while (!dirtyFields.isEmpty());
        return changedFields;
    }

    protected Map<FullPublicationField, FullPublicationProvider> getProviderIdx() {
        return providerIdx;
    }

    protected Multimap<FullPublicationField, FullPublicationField> getTransitiveDependencies() {
        return transitiveDependencies;
    }

    protected Multimap<FullPublicationField, FullPublicationField> getDirectDependencies() {
        return directDependencies;
    }

    public FullPublicationRepository getRepository() {
        return repository;
    }
}
