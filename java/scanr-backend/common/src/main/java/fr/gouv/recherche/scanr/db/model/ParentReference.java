/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.db.model;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Parent structure reference
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ParentReference {
    /**
     * Parent structure id
     */
    private String id;
    /**
     * Is this structure hierarchy exclusive
     */
    private boolean exclusive = true;

    public ParentReference(String id, boolean exclusive) {
        this.id = id;
        this.exclusive = exclusive;
    }

    public ParentReference() {
    }

    public ParentReference(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public boolean isExclusive() {
        return exclusive;
    }
}
