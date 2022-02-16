/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */

package fr.gouv.recherche.scanr.db.model;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * External ID with label => value
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ExternalId {
    private String id;
    private String type;

    public ExternalId() {
    }

    public ExternalId(String id, String type) {
        this.id = id;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
