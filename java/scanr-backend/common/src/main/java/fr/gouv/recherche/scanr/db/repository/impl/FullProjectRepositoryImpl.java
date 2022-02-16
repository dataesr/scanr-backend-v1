/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */

package fr.gouv.recherche.scanr.db.repository.impl;

import com.mongodb.WriteResult;
import fr.gouv.recherche.scanr.companies.util.MongoTemplateExtended;
import fr.gouv.recherche.scanr.db.model.full.FullProject;
import fr.gouv.recherche.scanr.db.model.full.FullProjectField;
import fr.gouv.recherche.scanr.db.repository.FullProjectRepositoryCustom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.HashSet;
import java.util.stream.Stream;

public class FullProjectRepositoryImpl implements FullProjectRepositoryCustom {

    @Autowired
    private MongoTemplateExtended mongo;

    @Override
    public FullProject findOne(String id, FullProjectField... fields) {
        Query query = new Query(Criteria.where("_id").is(id));
        if (fields != null && fields.length > 0) {
            query.fields().include("_id");
            for (FullProjectField field : fields) {
                query.fields().include(field.toAttributeName());
            }
        }
        return mongo.findOne(query, FullProject.class);
    }

    @Override
    public Stream<FullProject> streamAll(FullProjectField... fields) {
        Query query = new Query();
        if (fields != null && fields.length > 0) {
            query.fields().include("_id");
            for (FullProjectField field : fields) {
                query.fields().include(field.toAttributeName());
            }
        }
        return mongo.streamQuery(query, FullProject.class);
    }

    @Override
    public Stream<String> streamIdsToIndex() {
        Query query = new Query(Criteria.where("indexed").is(false));
        query.fields().include("_id");
        return mongo.streamQuery(query, FullProject.class).map(FullProject::getId);
    }

    @Override
    public boolean addDelayedFieldToRefresh(String id, FullProjectField... fields) {
        Query query = new Query(Criteria.where("_id").is(id));
        WriteResult result = mongo.updateFirst(query, new Update().set("indexed", false).addToSet("fieldsToRefresh").each((Object[]) fields), FullProject.class);
        return result.getN() == 1;
    }

    @Override
    public boolean notifyIndexed(String id) {
        Query query = new Query(Criteria.where("_id").is(id));
        WriteResult result = mongo.updateFirst(query, new Update().set("indexed", true).set("fieldsToRefresh", new HashSet()), FullProject.class);
        return result.getN() == 1;
    }

    @Override
    public Stream<String> selectAllIds() {
        Query q = new Query();
        q.fields().include("_id");
        return mongo.streamQuery(q, FullProject.class).map(FullProject::getId);
    }
}
