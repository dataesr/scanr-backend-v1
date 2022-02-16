/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.db.repository.impl;

import fr.gouv.recherche.scanr.companies.util.MongoTemplateExtended;
import fr.gouv.recherche.scanr.db.model.Structure;
import fr.gouv.recherche.scanr.db.repository.StructureRepositoryCustom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.stream.Stream;


public class StructureRepositoryImpl implements StructureRepositoryCustom {
    @Autowired
    private MongoTemplateExtended mongo;

    @Override
    public Stream<String> streamAllIds() {
        return streamIds(new Query());
    }

    @Override
    public Stream<String> streamAllIdsByLinkId(String websiteId) {
        return streamIds(new Query(Criteria.where("links._id").is(websiteId)));
    }

    protected Stream<String> streamIds(Query query) {
        query.fields().include("_id");
        return mongo.streamQuery(query, Structure.class).map(Structure::getId);
    }
}
