/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */

package fr.gouv.recherche.scanr.workflow.full;

import fr.gouv.recherche.scanr.db.model.full.FullPerson;
import fr.gouv.recherche.scanr.db.model.full.FullPersonField;

import java.util.Set;

/**
 * Interface of a full person provider service
 *
 * Each provider is able to produce one field
 */
public interface FullPersonProvider<E> {


    /**
     * Can the service provide something? It is a good idea to check for nullable entries or similar.
     *
     * @param person The current state of the full person
     * @return true if computeField can be called without restriction
     */
    default public boolean canProvide(FullPerson person) {
        return true;
    }
    /**
     * Compute the field from the current state of the person.
     *
     * The state of the person must be up to date wrt the dependencies
     *
     * @param person The current state of the full person
     * @return The new value of the field
     */
    public E computeField(FullPerson person);

    /**
     * Which field this service can provide?
     *
     * @return The field
     */
    public FullPersonField getField();

    /**
     * The list of fields that this provider depends on.
     *
     * @return The list
     */
    public Set<FullPersonField> getDependencies();
}
