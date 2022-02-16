/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.workflow.full;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import fr.gouv.recherche.scanr.companies.workflow.service.QueueService;
import fr.gouv.recherche.scanr.db.model.Structure;
import fr.gouv.recherche.scanr.db.model.Website;
import fr.gouv.recherche.scanr.db.model.full.FullStructure;
import fr.gouv.recherche.scanr.db.model.full.FullStructureField;
import fr.gouv.recherche.scanr.db.repository.*;
import fr.gouv.recherche.scanr.workflow.search.IndexStructureProcess;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 *
 */
@Service
public class FullStructureService {
    private static final Logger log = LoggerFactory.getLogger(FullStructureService.class);

    @Autowired(required = false)
    private List<FullStructureProvider> providers = Lists.newArrayList();

    @Autowired
    private FullStructureRepository repository;

    @Autowired
    private WebsiteRepository websiteRepository;

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
    private FullPersonRepository fullPersonRepository;
    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private QueueService service;

    private Map<FullStructureField, FullStructureProvider> providerIdx = new EnumMap<>(FullStructureField.class);
    private Multimap<FullStructureField, FullStructureField> transitiveDependencies = HashMultimap.create();
    private Multimap<FullStructureField, FullStructureField> directDependencies = HashMultimap.create();

    @PostConstruct
    protected void init() {
        // Compute direct dependencies graph & provider index
        for (FullStructureProvider<?> provider : providers) {
            FullStructureField field = provider.getField();
            if (field != null) {
                providerIdx.put(provider.getField(), provider);

                log.info("Registering provider {} for field {}", provider.getClass().getName(), provider.getField());
                Set<FullStructureField> dependencies = provider.getDependencies();
                if (dependencies != null) {
                    for (FullStructureField dependencyField : provider.getDependencies()) {
                        directDependencies.put(dependencyField, provider.getField());
                    }
                }
            }
        }

        computeTransitiveDependencies();
    }

    protected void computeTransitiveDependencies() {
        // Compute the transitive dependencies
        for (FullStructureField field : directDependencies.keySet()) {
            Set<FullStructureField> deps = Sets.newHashSet();
            fillDependencies(field, directDependencies, deps);
            transitiveDependencies.putAll(field, deps);
        }
    }

    private static void fillDependencies(FullStructureField field, Multimap<FullStructureField, FullStructureField> dep,
                                         Set<FullStructureField> result) {
        if (!dep.containsKey(field)) {
            return;
        }
        for (FullStructureField depField : dep.get(field)) {
            result.add(depField);
            fillDependencies(depField, dep, result);
        }
    }

    /**
     * Create a transaction on an identifier. When you are inside the context of a TX you are sure that you are alone
     * working on this structure. Also, the save will throw a full structure updated message.
     * <p/>
     * <pre>
     *    try(FullStructureTransaction tx = service.tx("FR-123456789", false)) {
     *        tx.setField(...);
     *        tx.save();
     *    }
     * </pre>
     *
     * @param id The structure identifier
     * @param failOnAbsent If true,
     * @return The TX object
     */
    public FullStructureTransaction tx(String id, boolean failOnAbsent) {
        if (StringUtils.isEmpty(id)) {
            throw new IllegalArgumentException("Id can't null or empty.");
        }

        try {
            Structure structure = structureRepository.findOne(id);
            if (structure == null) {
                if (failOnAbsent) {
                    throw new NoSuchElementException("Unknown structure " + id);
                } else {
                    return null;
                }
            }
            log.debug("Creating full structure " + id);

            // Create the new full structure
            FullStructure fullStructure = new FullStructure(id);
            BeanUtils.copyProperties(structure, fullStructure);

            Set<FullStructureField> changedFields = cleanDirtyFields(fullStructure, Sets.newHashSet(FullStructureField.values()));
            FullStructureTransaction fst = new FullStructureTransaction(this, fullStructure, true);
            return fst;
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
    public void refresh(String id, FullStructureField field) {
        try (FullStructureTransaction tx = tx(id, true)) {
            tx.refresh(field);
            tx.save(false, false);
        }
    }

    /**
     * Refresh the website for the full structure
     *
     * @param linkId The link identifier that is contained in a full structure
     */
    public void refreshWebsite(String linkId) {
        List<FullStructure> fullStructures = repository.findByLinkId(linkId);

        fullStructures.forEach(fullStructure -> {
            List<Website> websites = fullStructure.getWebsites();

            // Check that the list is iniatilized
            if (websites == null) {
                websites = new ArrayList<>();
            }

            Website website = websiteRepository.findOne(linkId);

            // If the website is not created yet, we add an empty website with the id and add the website to the list of fields to be refresh
            if (website == null) {
                website = new Website();
                website.setId(linkId);
            }

            websites.add(website);
            fullStructure.setWebsites(websites);
            fullStructure.getFieldsToRefresh().add(FullStructureField.WEBSITES);
            repository.save(fullStructure);

            // We need to reindex the structure after these operations (from WebsiteAnalysisService->analysisEnd)
            service.push(fullStructure.getId(), IndexStructureProcess.QUEUE, null);
        });
    }

    /**
     * Utility method that allows a user to refresh a field easily
     * Does not throw IAE if the structure doesnt exist
     *
     * @param id    The identifier
     * @param field The field to update
     */
    public void refreshIfExists(String id, FullStructureField field) {
        try (FullStructureTransaction tx = tx(id, false)) {
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
    public <E> void getAndUpdateField(String id, FullStructureField field, E data) {
        try (FullStructureTransaction tx = tx(id, true)) {
            tx.setField(field, data);
            tx.save(false, false);
        }
    }

    /**
     * Utility method to ensure that the id is created in the base.
     *
     * @param id The structure identifier
     */
    public void ensureCreated(String id) {
        try (FullStructureTransaction tx = tx(id, true)) {
            tx.save(false, false);
        }
    }

    /**
     * Utility method to ensure that the id is created in the base.
     *
     * @param id The structure identifier
     */
    public void delayedRefresh(String id, FullStructureField... fields) {
        try {
            repository.addDelayedFieldToRefresh(id, fields);
        } finally {
        }
    }

    /**
     * Utility method to ensure that the id is created in the base.
     *
     * @param id The structure identifier
     */
    public void notifyIndexed(String id) {
        try {
            repository.notifyIndexed(id);
        } finally {
        }
    }

    protected void delete(FullStructureTransaction tx, boolean forcePropagation) {
        log.debug("[{}] Removing full structure ", tx.getData().getId());
        repository.delete(tx.getData().getId());

        // propagate notification
        if (forcePropagation) {
            // Notify the index that the structure needs to be updated
            service.push(tx.getData().getId(), IndexStructureProcess.QUEUE);
        }
    }

    protected void save(FullStructureTransaction tx, boolean forcePropagation, boolean forIndex) {
        repository.save(tx.getData());
        log.debug("[{}] Saving", tx.getData().getId());
    }

    /**
     * Set the field from a full structure and update all the fields based on their providers.
     *
     * @param Structure The full structure object
     * @param field     The field to update
     * @param data      The data to put
     * @param <E>       The data type
     * @return The list of updated fields (the given field is included)
     */
    protected <E> Set<FullStructureField> setField(FullStructure Structure, FullStructureField field, E data) {
        field.setter().accept(Structure, data);
        Set<FullStructureField> changedFields = cleanDirtyFields(Structure, Sets.newHashSet(directDependencies.get(field)));
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
     * @param structure   The full structure object
     * @param dirtyFields The dirty fields to check or clean
     * @return The list of affected fields
     */
    protected Set<FullStructureField> cleanDirtyFields(FullStructure structure, Set<FullStructureField> dirtyFields) {
        Set<FullStructureField> changedFields = Sets.newHashSet();
        if (dirtyFields.isEmpty())
            return changedFields;

        Multimap<FullStructureField, FullStructureField> reverseDependencies = HashMultimap.create();

        // While we still have fields to clean
        do {
            // Compute the reverse dependencies graph
            reverseDependencies.clear();
            for (FullStructureField dirtyField : dirtyFields) {
                transitiveDependencies.get(dirtyField).stream()
                        .filter(dirtyFields::contains)
                        .forEach(dirtyDep -> reverseDependencies.put(dirtyDep, dirtyField));
            }

            // Check for any field that has no dependency
            FullStructureField dirtyField = dirtyFields.stream().filter(it -> reverseDependencies.get(it).isEmpty()).findAny().get();

            // Get the provider
            FullStructureProvider provider = providerIdx.get(dirtyField);
            if (provider == null) {
                throw new IllegalStateException("No provider were found for " + dirtyField);
            }

            // Try to set the field
            Object old = dirtyField.getter().apply(structure);

            log.trace("[{}] Calling provider for field {}", structure.getId(), dirtyField);
            Object newValue = null;
            // Update if necessary
            if (provider.canProvide(structure)) {
                newValue = provider.computeField(structure);
            }
            // If old and new are considered equal, there no need to propagate the changes
            if (old == null && newValue != null || old != null && !old.equals(newValue)) {
                log.trace("[{}] Field {} has changed, propagating to dependencies", structure.getId(), dirtyField);
                dirtyField.setter().accept(structure, newValue);
                changedFields.add(dirtyField);

                // Add the newly dirty fields
                dirtyFields.addAll(directDependencies.get(dirtyField));
            } else if (newValue == null) {
                log.trace("[{}] Field {} remains null", structure.getId(), dirtyField);
            }

            // Now this is clean
            dirtyFields.remove(dirtyField);
        } while (!dirtyFields.isEmpty());
        return changedFields;
    }

    protected Map<FullStructureField, FullStructureProvider> getProviderIdx() {
        return providerIdx;
    }

    protected Multimap<FullStructureField, FullStructureField> getTransitiveDependencies() {
        return transitiveDependencies;
    }

    protected Multimap<FullStructureField, FullStructureField> getDirectDependencies() {
        return directDependencies;
    }

    public FullStructureRepository getRepository() {
        return repository;
    }
}
