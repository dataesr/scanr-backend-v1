/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.db.repository.impl;

import fr.gouv.recherche.scanr.companies.util.MongoTemplateExtended;
import fr.gouv.recherche.scanr.db.model.publication.Publication;
import fr.gouv.recherche.scanr.db.model.publication.PublicationIdentifiers;
import fr.gouv.recherche.scanr.db.repository.PublicationRepositoryCustom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;


public class PublicationRepositoryImpl implements PublicationRepositoryCustom {
    @Autowired
    private MongoTemplateExtended mongo;

    @Override
    public List<Publication> findSimilar(String id, PublicationIdentifiers identifiers) {
        // Conditions list treated as OR
        List<Criteria> conditions = new LinkedList<>();

        // Id can be null if we want to search only by identifiers
        addIdCriteria(conditions, "_id", id);

        addIdCriteria(conditions, "identifiers.doi", identifiers.getDoi());
        addIdCriteria(conditions, "identifiers.hal", identifiers.getHal());
        List<String> oaiIds = identifiers.getOai();
        if (oaiIds != null) {
            for (String oai : oaiIds) {
                addIdCriteria(conditions, "identifiers.oai", oai);
            }
        }
        addIdCriteria(conditions, "identifiers.prodinra", identifiers.getProdinra());
        List<String> patentIds = identifiers.getPatent();
        if (patentIds != null) {
            for (String patent : patentIds) {
                addIdCriteria(conditions, "identifiers.patent", patent);
            }
        }

        if (conditions.isEmpty()) {
            throw new IllegalStateException("Void publication given to find similar");
        }

        // Create the query as a or
        Query query = new Query(new Criteria().orOperator(conditions.toArray(new Criteria[conditions.size()])));
        return mongo.find(query, Publication.class);
    }

    @Override
    public Stream<Publication> streamEntities() {
        Query q = new Query();
        q.fields().include("_id").include("title").include("identifiers.patent");
        return mongo.streamQuery(q, Publication.class);
    }

    @Override
    public Stream<String> streamAllIds() {
        return streamIds(new Query());
    }

    protected Stream<String> streamIds(Query query) {
        query.fields().include("_id");
        return mongo.streamQuery(query, Publication.class).map(Publication::getId);
    }

    protected void addIdCriteria(List<Criteria> conditions, final String field, String fieldValue) {
        if (fieldValue != null) {
            conditions.add(Criteria.where(field).is(fieldValue));
        }
    }
}
