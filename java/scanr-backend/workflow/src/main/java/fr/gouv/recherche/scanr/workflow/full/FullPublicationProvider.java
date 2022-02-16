/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */

package fr.gouv.recherche.scanr.workflow.full;

import fr.gouv.recherche.scanr.db.model.full.FullPublication;
import fr.gouv.recherche.scanr.db.model.full.FullPublicationField;

import java.util.Set;

/**
 * Interface of a full publication provider service
 *
 * Each provider is able to produce one field
 */
public interface FullPublicationProvider<E> {

    /**
     * Can the service provide something? It is a good idea to check for nullable entries or similar.
     *
     * @param publication The current state of the full publication
     * @return true if computeField can be called without restriction
     */
    default public boolean canProvide(FullPublication publication) {
        return true;
    }
    /**
     * Compute the field from the current state of the publication.
     *
     * The state of the publication must be up to date wrt the dependencies
     *
     * @param publication The current state of the full publication
     * @return The new value of the field
     */
    public E computeField(FullPublication publication);

    /**
     * Which field this service can provide?
     *
     * @return The field
     */
    public FullPublicationField getField();

    /**
     * The list of fields that this provider depends on.
     *
     * @return The list
     */
    public Set<FullPublicationField> getDependencies();
}
