/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.companies.repository.mongo;

import fr.gouv.recherche.scanr.companies.model.error.ErrorMessage;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 *
 */

public interface ErrorRepository extends PagingAndSortingRepository<ErrorMessage, ObjectId>, ErrorRepositoryCustom {
    List<ErrorMessage> findByQueue(String queue);

    Page<ErrorMessage> findByQueue(String queue, Pageable page);

    @Query(value = "{'queue': ?0}", count = true)
    Long countByQueue(String queue);
}
