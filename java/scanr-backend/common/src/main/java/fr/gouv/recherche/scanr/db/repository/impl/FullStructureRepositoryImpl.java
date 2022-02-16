/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.db.repository.impl;

import com.mongodb.WriteResult;
import fr.gouv.recherche.scanr.companies.util.MongoTemplateExtended;
import fr.gouv.recherche.scanr.db.model.full.FullStructure;
import fr.gouv.recherche.scanr.db.model.full.FullStructureField;
import fr.gouv.recherche.scanr.db.repository.FullStructureRepositoryCustom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.HashSet;
import java.util.stream.Stream;


public class FullStructureRepositoryImpl implements FullStructureRepositoryCustom {
    @Autowired
    private MongoTemplateExtended mongo;

    @Override
    public FullStructure findOne(String id, FullStructureField... fields) {
        Query query = new Query(Criteria.where("_id").is(id));
        if (fields != null && fields.length > 0) {
            query.fields().include("_id");
            for (FullStructureField field : fields) {
                query.fields().include(field.toAttributeName());
            }
        }
        return mongo.findOne(query, FullStructure.class);
    }

    @Override
    public Stream<FullStructure> streamAll(FullStructureField... fields) {
        Query query = new Query();
        if (fields != null && fields.length > 0) {
            query.fields().include("_id");
            for (FullStructureField field : fields) {
                query.fields().include(field.toAttributeName());
            }
        }
        return mongo.streamQuery(query, FullStructure.class);
    }

    @Override
    public Stream<String> streamIdsToIndex() {
        Query query = new Query(Criteria.where("indexed").is(false));
        query.fields().include("_id");
        return mongo.streamQuery(query, FullStructure.class).map(FullStructure::getId);
    }

    @Override
    public boolean addDelayedFieldToRefresh(String id, FullStructureField... fields) {
        Query query = new Query(Criteria.where("_id").is(id));
        WriteResult result = mongo.updateFirst(query, new Update().set("indexed", false).addToSet("fieldsToRefresh").each((Object[]) fields), FullStructure.class);
        return result.getN() == 1;
    }

    @Override
    public boolean notifyIndexed(String id) {
        Query query = new Query(Criteria.where("_id").is(id));
        WriteResult result = mongo.updateFirst(query, new Update().set("indexed", true).set("fieldsToRefresh", new HashSet()), FullStructure.class);
        return result.getN() == 1;
    }

    @Override
    public Stream<String> selectAllIds() {
        Query q = new Query();
        q.fields().include("_id");
        return mongo.streamQuery(q, FullStructure.class).map(FullStructure::getId);
    }
}
