/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.companies.repository.mongo;

import fr.gouv.recherche.scanr.companies.model.scheduler.ScheduledMessage;

import java.util.Date;
import java.util.stream.Stream;

/**
 *
 */
public interface ScheduledMessageRepositoryCustom {
    Stream<ScheduledMessage> findAllForExecution(Date now);
    void updateNextExecution(String id, Date next);

    Stream<String> findAllFromProvider(String provider);
}
