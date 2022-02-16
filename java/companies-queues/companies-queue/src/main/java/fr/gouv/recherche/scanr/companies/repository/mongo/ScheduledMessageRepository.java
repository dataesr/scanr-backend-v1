/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.companies.repository.mongo;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import fr.gouv.recherche.scanr.companies.model.scheduler.ScheduledMessage;

/**
 *
 */

public interface ScheduledMessageRepository extends PagingAndSortingRepository<ScheduledMessage, String>, ScheduledMessageRepositoryCustom {
}
