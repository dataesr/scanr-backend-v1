/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.db.repository;

import fr.gouv.recherche.scanr.db.model.Website;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface WebsiteRepository extends MongoRepository<Website, String>, WebsiteRepositoryCustom {
}
