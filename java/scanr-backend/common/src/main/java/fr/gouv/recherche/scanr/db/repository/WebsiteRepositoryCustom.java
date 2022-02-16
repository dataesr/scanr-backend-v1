/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.db.repository;

import fr.gouv.recherche.scanr.db.model.Website;

import java.util.stream.Stream;


public interface WebsiteRepositoryCustom {
    Stream<Website> streamAll();
}
