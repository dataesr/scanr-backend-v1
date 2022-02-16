/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */

package fr.gouv.recherche.scanr.db.repository;

import fr.gouv.recherche.scanr.db.model.full.FullPublication;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FullPublicationRepository extends MongoRepository<FullPublication, String>, FullPublicationRepositoryCustom {

}
