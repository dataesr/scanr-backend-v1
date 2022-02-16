/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.companies.repository.mongo.impl;

import fr.gouv.recherche.scanr.companies.model.scheduler.ExecutionStatus;
import fr.gouv.recherche.scanr.companies.model.scheduler.ScheduledMessage;
import fr.gouv.recherche.scanr.companies.repository.mongo.ScheduledMessageRepositoryCustom;
import fr.gouv.recherche.scanr.companies.util.MongoTemplateExtended;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.Date;
import java.util.stream.Stream;

/**
 *
 */
public class ScheduledMessageRepositoryImpl implements ScheduledMessageRepositoryCustom {

    @Autowired
    private MongoTemplateExtended mongo;

    @Override
    public Stream<ScheduledMessage> findAllForExecution(Date now) {
        Query query = new Query(Criteria.where("nextExecution").lte(now).and("status").is(ExecutionStatus.PLANNED.name()));
        query.with(new Sort(Sort.Direction.ASC, "nextExecution"));
        return mongo.streamQuery(query, ScheduledMessage.class);
    }

    @Override
    public void updateNextExecution(String id, Date next) {
        mongo.updateFirst(new Query(Criteria.where("_id").is(id)), new Update().set("nextExecution", next),
                        ScheduledMessage.class);
    }

    @Override
    public Stream<String> findAllFromProvider(String provider) {
        Query query = new Query(Criteria.where("provider").is(provider));
        query.fields().include("_id");
        return mongo.streamQuery(query, ScheduledMessage.class).map(ScheduledMessage::getId);
    }
}
