/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.db.repository;

import fr.gouv.recherche.scanr.db.model.publication.Publication;
import fr.gouv.recherche.scanr.db.model.publication.PublicationIdentifiers;

import java.util.List;
import java.util.stream.Stream;


public interface PublicationRepositoryCustom {
    /**
     * Find a publication that matches this one iff:
     *  - their ids are similar
     *  - they share one of their identifiers
     *
     * @param id
     * @param identifiers
     * @return A list of matches (there should be only one?)
     */
    List<Publication> findSimilar(String id, PublicationIdentifiers identifiers);

    Stream<String> streamAllIds();
    Stream<Publication> streamEntities();
}
