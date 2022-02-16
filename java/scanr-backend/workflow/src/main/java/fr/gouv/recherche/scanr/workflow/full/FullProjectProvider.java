/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */

package fr.gouv.recherche.scanr.workflow.full;

import fr.gouv.recherche.scanr.db.model.full.FullProject;
import fr.gouv.recherche.scanr.db.model.full.FullProjectField;

import java.util.Set;

/**
 * Interface of a full project provider service
 *
 * Each provider is able to produce one field
 */
public interface FullProjectProvider<E> {

    /**
     * Can the service provide something? It is a good idea to check for nullable entries or similar.
     *
     * @param project The current state of the full project
     * @return true if computeField can be called without restriction
     */
    default public boolean canProvide(FullProject project) {
        return true;
    }
    /**
     * Compute the field from the current state of the project.
     *
     * The state of the project must be up to date wrt the dependencies
     *
     * @param project The current state of the full project
     * @return The new value of the field
     */
    public E computeField(FullProject project);

    /**
     * Which field this service can provide?
     *
     * @return The field
     */
    public FullProjectField getField();

    /**
     * The list of fields that this provider depends on.
     *
     * @return The list
     */
    public Set<FullProjectField> getDependencies();
}
