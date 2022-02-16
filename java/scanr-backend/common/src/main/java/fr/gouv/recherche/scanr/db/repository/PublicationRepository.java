/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.db.repository;

import fr.gouv.recherche.scanr.db.model.publication.Publication;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

public interface PublicationRepository extends MongoRepository<Publication, String>, PublicationRepositoryCustom {

    @Query(value = "{'authors.person._id': ?0}")
    List<Publication> findPublicationByPersonId(String personId);

    @Query(value = "{'projects._id': ?0}")
    List<Publication> findPublicationByProjectId(String projectId);

    @Query(value = "{'affiliations._id': ?0}")
    List<Publication> findPublicationByStructureId(String structureId);

    @Query(value = "{'affiliations._id': ?0}", fields = "{_id: 1, title:1, keywords:1}")
    List<Publication> findPublicationUltraLightByStructureId(String structureId, Pageable pageable);

    Stream<String> streamAllIds();

    @Query(value = "{_id: {'$in': ?0}}", fields = "{_id: 1, type:1, title:1, authors:1, authorsCount:1, externalIds:1, isOa:1, publicationDate:1, citedBy:1}")
    List<Publication> findByIdsLight(Collection<String> id);

    @Query(value = "{_id: ?0}", fields = "{_id: 1, title:1, keywords:1}")
    Publication findByIdLight(String id);

    @Query(value = "{_id: {'$in': ?0}}", delete = true)
    void deleteByIds(Collection<String> id);

}
