/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.config.elasticsearch;

import com.sword.utils.elasticsearch.config.IIndex;

import java.util.Calendar;

public enum EsIndexEnum implements IIndex {

    STRUCTURE("structures"),
    PUBLICATION("publications"),
    PROJECT("projects"),
    PERSON("persons"),

    // Index utilisés pour la création, avec ajout d'un alias du nom des types ci-dessus
    CREATE_NEW_INDEX_STRUCTURE(STRUCTURE.getName() + String.valueOf(Calendar.getInstance().getTimeInMillis())),
    CREATE_NEW_INDEX_PROJECT(PROJECT.getName() + String.valueOf(Calendar.getInstance().getTimeInMillis())),
    CREATE_NEW_INDEX_PERSON(PERSON.getName() + String.valueOf(Calendar.getInstance().getTimeInMillis())),
    CREATE_NEW_INDEX_PUBLICATION(PUBLICATION.getName() + String.valueOf(Calendar.getInstance().getTimeInMillis()));

    protected String name;

    private EsIndexEnum(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }
}
