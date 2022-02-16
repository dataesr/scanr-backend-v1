/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.db.repository.impl;

import fr.gouv.recherche.scanr.companies.util.MongoTemplateExtended;
import fr.gouv.recherche.scanr.db.model.Project;
import fr.gouv.recherche.scanr.db.repository.ProjectRepositoryCustom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;
import java.util.stream.Stream;


public class ProjectRepositoryImpl implements ProjectRepositoryCustom {

    @Autowired
    private MongoTemplateExtended mongoTemplate;

    @Override
    public Stream<Project> streamEntities() {
        Query query = new Query();
        query.fields().include("name").include("acronym").include("label");
        return mongoTemplate.streamQuery(query, Project.class);
    }

    @Override
    public List<Project> findByAcronymLike(String regex, int limit) {
        Query filter = new Query(Criteria.where("acronym").regex(regex, "i"));
        filter.fields().include("name").include("acronym").include("label");
        filter.limit(limit);
        return mongoTemplate.find(filter, Project.class);
    }

    @Override
    public Stream<String> streamAllIds() {
        return streamIds(new Query());
    }

    protected Stream<String> streamIds(Query query) {
        query.fields().include("_id");
        return mongoTemplate.streamQuery(query, Project.class).map(Project::getId);
    }
}
