/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */

package fr.gouv.recherche.scanr.db.repository.impl;

import fr.gouv.recherche.scanr.companies.util.MongoTemplateExtended;
import fr.gouv.recherche.scanr.db.model.Person;
import fr.gouv.recherche.scanr.db.repository.PersonRepositoryCustom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Query;

import java.util.stream.Stream;

public class PersonRepositoryImpl implements PersonRepositoryCustom {

    @Autowired
    private MongoTemplateExtended mongoTemplate;

    @Override
    public Stream<Person> streamEntities() {
        Query query = new Query();
        query.fields().include("name").include("acronym").include("label");
        return mongoTemplate.streamQuery(query, Person.class);
    }

    @Override
    public Stream<String> streamAllIds() {
        return streamIds(new Query());
    }

    protected Stream<String> streamIds(Query query) {
        query.fields().include("_id");
        return mongoTemplate.streamQuery(query, Person.class).map(Person::getId);
    }

}
