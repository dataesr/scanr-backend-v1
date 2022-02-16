/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */

package fr.gouv.recherche.scanr.db.repository;

import fr.gouv.recherche.scanr.db.model.Person;

import java.util.stream.Stream;

public interface PersonRepositoryCustom {
    Stream<Person> streamEntities();
    Stream<String> streamAllIds();
}
