/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.db.repository.impl;

import fr.gouv.recherche.scanr.companies.util.MongoTemplateExtended;
import fr.gouv.recherche.scanr.db.model.Website;
import fr.gouv.recherche.scanr.db.repository.WebsiteRepositoryCustom;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.stream.Stream;


public class WebsiteRepositoryImpl implements WebsiteRepositoryCustom {
    @Autowired
    private MongoTemplateExtended templateExtended;

    @Override
    public Stream<Website> streamAll() {
        return templateExtended.streamAll(Website.class);
    }
}
