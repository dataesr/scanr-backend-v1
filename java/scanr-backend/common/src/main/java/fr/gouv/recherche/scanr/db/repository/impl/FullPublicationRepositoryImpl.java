/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */

package fr.gouv.recherche.scanr.db.repository.impl;

import com.mongodb.WriteResult;
import fr.gouv.recherche.scanr.companies.util.MongoTemplateExtended;
import fr.gouv.recherche.scanr.db.model.full.FullPublication;
import fr.gouv.recherche.scanr.db.model.full.FullPublicationField;
import fr.gouv.recherche.scanr.db.repository.FullPublicationRepositoryCustom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.HashSet;
import java.util.stream.Stream;

public class FullPublicationRepositoryImpl implements FullPublicationRepositoryCustom {

    @Autowired
    private MongoTemplateExtended mongo;

    @Override
    public FullPublication findOne(String id, FullPublicationField... fields) {
        Query query = new Query(Criteria.where("_id").is(id));
        if (fields != null && fields.length > 0) {
            query.fields().include("_id");
            for (FullPublicationField field : fields) {
                query.fields().include(field.toAttributeName());
            }
        }
        return mongo.findOne(query, FullPublication.class);
    }

    @Override
    public Stream<FullPublication> streamAll(FullPublicationField... fields) {
        Query query = new Query();
        if (fields != null && fields.length > 0) {
            query.fields().include("_id");
            for (FullPublicationField field : fields) {
                query.fields().include(field.toAttributeName());
            }
        }
        return mongo.streamQuery(query, FullPublication.class);
    }

    @Override
    public Stream<String> streamIdsToIndex() {
        Query query = new Query(Criteria.where("indexed").is(false));
        query.fields().include("_id");
        return mongo.streamQuery(query, FullPublication.class).map(FullPublication::getId);
    }

    @Override
    public boolean addDelayedFieldToRefresh(String id, FullPublicationField... fields) {
        Query query = new Query(Criteria.where("_id").is(id));
        WriteResult result = mongo.updateFirst(query, new Update().set("indexed", false).addToSet("fieldsToRefresh").each((Object[]) fields), FullPublication.class);
        return result.getN() == 1;
    }

    @Override
    public boolean notifyIndexed(String id) {
        Query query = new Query(Criteria.where("_id").is(id));
        WriteResult result = mongo.updateFirst(query, new Update().set("indexed", true).set("fieldsToRefresh", new HashSet()), FullPublication.class);
        return result.getN() == 1;
    }

    @Override
    public Stream<String> selectAllIds() {
        Query q = new Query();
        q.fields().include("_id");
        return mongo.streamQuery(q, FullPublication.class).map(FullPublication::getId);
    }
}
