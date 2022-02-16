/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.db.repository;

import fr.gouv.recherche.scanr.db.model.full.FullStructure;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface FullStructureRepository extends MongoRepository<FullStructure, String>, FullStructureRepositoryCustom {
    @Query(value = "{_id: {'$in': ?0}}", fields = "{_id: 1, 'websites.twitter.profilePictureUrl':1}")
    List<FullStructure> findByIdsLightWithTwitterLogo(List<String> twitterLogoToFetchStructures);

    @Query(value = "{'links._id': ?0}")
    List<FullStructure> findByLinkId(String linkId);
}
