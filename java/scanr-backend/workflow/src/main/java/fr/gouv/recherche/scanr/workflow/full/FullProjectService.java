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
import fr.gouv.recherche.scanr.db.model.Project;
import fr.gouv.recherche.scanr.db.model.Structure;
import fr.gouv.recherche.scanr.db.model.StructureSpinoff;
import fr.gouv.recherche.scanr.db.model.full.*;
import fr.gouv.recherche.scanr.db.model.publication.Publication;
import fr.gouv.recherche.scanr.db.repository.*;
import fr.gouv.recherche.scanr.workflow.search.IndexProjectProcess;
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
public class FullProjectService {

    private static final Logger log = LoggerFactory.getLogger(FullProjectService.class);

    private Striped<Lock> lock = Striped.lock(64);

    @Autowired(required = false)
    private List<FullProjectProvider> providers = Lists.newArrayList();

    @Autowired
    private FullProjectRepository fullProjectRepository;
    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private FullPersonService fullPersonService;

    @Autowired
    private FullPublicationRepository fullPublicationRepository;
    @Autowired
    private PublicationRepository publicationRepository;

    @Autowired
    private FullStructureService fullStructureService;
    @Autowired
    private FullStructureRepository fullStructureRepository;
    @Autowired
    private StructureRepository structureRepository;

    @Autowired
    private QueueService service;

    private Map<FullProjectField, FullProjectProvider> providerIdx = new EnumMap<>(FullProjectField.class);
    private Multimap<FullProjectField, FullProjectField> transitiveDependencies = HashMultimap.create();
    private Multimap<FullProjectField, FullProjectField> directDependencies = HashMultimap.create();

    @PostConstruct
    protected void init() {
        // Compute direct dependencies graph & provider index
        for (FullProjectProvider<?> provider : providers) {
            FullProjectField field = provider.getField();
            if (field != null) {
                providerIdx.put(provider.getField(), provider);

                log.info("Registering provider {} for field {}", provider.getClass().getName(), provider.getField());
                Set<FullProjectField> dependencies = provider.getDependencies();
                if (dependencies != null) {
                    for (FullProjectField dependencyField : provider.getDependencies()) {
                        directDependencies.put(dependencyField, provider.getField());
                    }
                }
            }
        }

        computeTransitiveDependencies();
    }

    protected void computeTransitiveDependencies() {
        // Compute the transitive dependencies
        for (FullProjectField field : directDependencies.keySet()) {
            Set<FullProjectField> deps = Sets.newHashSet();
            fillDependencies(field, directDependencies, deps);
            transitiveDependencies.putAll(field, deps);
        }
    }

    private static void fillDependencies(FullProjectField field, Multimap<FullProjectField, FullProjectField> dep,
                                         Set<FullProjectField> result) {
        if (!dep.containsKey(field)) {
            return;
        }
        for (FullProjectField depField : dep.get(field)) {
            result.add(depField);
            fillDependencies(depField, dep, result);
        }
    }

    /**
     * Create a transaction on an identifier. When you are inside the context of a TX you are sure that you are alone
     * working on this structure. Also, the save will throw a full project updated message.
     * <p/>
     * <pre>
     *    try(FullProjectTransaction tx = service.tx("FR-123456789", false)) {
     *        tx.setField(...);
     *        tx.save();
     *    }
     * </pre>
     *
     * @param id The project identifier
     * @param failOnAbsent If true,
     * @return The TX object
     */
    public FullProjectTransaction tx(String id, boolean failOnAbsent) {
        if (StringUtils.isEmpty(id)) {
            throw new IllegalArgumentException("Id can't null or empty.");
        }

        try {
            Project project = projectRepository.findOne(id);
            if (project == null) {
                if (failOnAbsent) {
                    throw new NoSuchElementException("Unknown project " + id);
                } else {
                    return null;
                }
            }
            log.debug("Creating full project " + id);

            // Create the new full project
            FullProject newFullProject = new FullProject(id);
            BeanUtils.copyProperties(project, newFullProject);

            Set<FullProjectField> changedFields = cleanDirtyFields(newFullProject, Sets.newHashSet(FullProjectField.values()));
            FullProjectTransaction transaction = new FullProjectTransaction(this, newFullProject, true);
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
    public void refresh(String id, FullProjectField field) {
        try (FullProjectTransaction tx = tx(id, true)) {
            tx.refresh(field);
            tx.save(false, false);
        }
    }

    /**
     * Utility method that allows a user to refresh a field easily
     * Does not throw IAE if the project doesnt exist
     *
     * @param id    The identifier
     * @param field The field to update
     */
    public void refreshIfExists(String id, FullProjectField field) {
        try (FullProjectTransaction tx = tx(id, false)) {
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
    public <E> void getAndUpdateField(String id, FullProjectField field, E data) {
        try (FullProjectTransaction tx = tx(id, true)) {
            tx.setField(field, data);
            tx.save(false, false);
        }
    }

    /**
     * Utility method to ensure that the id is created in the base.
     *
     * @param id The project identifier
     */
    public void ensureCreated(String id) {
        try (FullProjectTransaction tx = tx(id, true)) {
            tx.save(false, false);
        }
    }

    /**
     * Utility method to ensure that the id is created in the base.
     *
     * @param id The project identifier
     */
    public void delayedRefresh(String id, FullProjectField... fields) {
        try {
            fullProjectRepository.addDelayedFieldToRefresh(id, fields);
        } finally {
        }
    }

    /**
     * Utility method to ensure that the id is created in the base.
     *
     * @param id The project identifier
     */
    public void notifyIndexed(String id) {
        try {
            fullProjectRepository.notifyIndexed(id);
        } finally {
        }
    }

    protected void delete(FullProjectTransaction tx, boolean forcePropagation) {
        log.debug("[{}] Removing full project ", tx.getData().getId());
        fullProjectRepository.delete(tx.getData().getId());

        // propagate notification
        if (forcePropagation) {
            // Notify the index that the structure needs to be updated
            service.push(tx.getData().getId(), IndexProjectProcess.QUEUE);
        }
    }

    protected void save(FullProjectTransaction tx, boolean forcePropagation, boolean forIndex) {
        fullProjectRepository.save(tx.getData());
        log.debug("[{}] Saving", tx.getData().getId());
    }

    /**
     * Set the field from a full project and update all the fields based on their providers.
     *
     * @param project The full project object
     * @param field     The field to update
     * @param data      The data to put
     * @param <E>       The data type
     * @return The list of updated fields (the given field is included)
     */
    protected <E> Set<FullProjectField> setField(FullProject project, FullProjectField field, E data) {
        field.setter().accept(project, data);
        Set<FullProjectField> changedFields = cleanDirtyFields(project, Sets.newHashSet(directDependencies.get(field)));
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
     * @param project   The full project object
     * @param dirtyFields The dirty fields to check or clean
     * @return The list of affected fields
     */
    protected Set<FullProjectField> cleanDirtyFields(FullProject project, Set<FullProjectField> dirtyFields) {
        Set<FullProjectField> changedFields = Sets.newHashSet();
        if (dirtyFields.isEmpty())
            return changedFields;

        Multimap<FullProjectField, FullProjectField> reverseDependencies = HashMultimap.create();

        // While we still have fields to clean
        do {
            // Compute the reverse dependencies graph
            reverseDependencies.clear();
            for (FullProjectField dirtyField : dirtyFields) {
                transitiveDependencies.get(dirtyField).stream()
                        .filter(dirtyFields::contains)
                        .forEach(dirtyDep -> reverseDependencies.put(dirtyDep, dirtyField));
            }

            // Check for any field that has no dependency
            FullProjectField dirtyField = dirtyFields.stream().filter(it -> reverseDependencies.get(it).isEmpty()).findAny().get();

            // Get the provider
            FullProjectProvider provider = providerIdx.get(dirtyField);
            if (provider == null) {
                throw new IllegalStateException("No provider were found for " + dirtyField);
            }

            // Try to set the field
            Object old = dirtyField.getter().apply(project);

            log.trace("[{}] Calling provider for field {}", project.getId(), dirtyField);
            Object newValue = null;
            // Update if necessary
            if (provider.canProvide(project)) {
                newValue = provider.computeField(project);
            }
            // If old and new are considered equal, there no need to propagate the changes
            if (old == null && newValue != null || old != null && !old.equals(newValue)) {
                log.trace("[{}] Field {} has changed, propagating to dependencies", project.getId(), dirtyField);
                dirtyField.setter().accept(project, newValue);
                changedFields.add(dirtyField);

                // Add the newly dirty fields
                dirtyFields.addAll(directDependencies.get(dirtyField));
            } else if (newValue == null) {
                log.trace("[{}] Field {} remains null", project.getId(), dirtyField);
            }

            // Now this is clean
            dirtyFields.remove(dirtyField);
        } while (!dirtyFields.isEmpty());
        return changedFields;
    }

    protected Map<FullProjectField, FullProjectProvider> getProviderIdx() {
        return providerIdx;
    }

    protected Multimap<FullProjectField, FullProjectField> getTransitiveDependencies() {
        return transitiveDependencies;
    }

    protected Multimap<FullProjectField, FullProjectField> getDirectDependencies() {
        return directDependencies;
    }

    public FullProjectRepository getFullProjectRepository() {
        return fullProjectRepository;
    }
}
