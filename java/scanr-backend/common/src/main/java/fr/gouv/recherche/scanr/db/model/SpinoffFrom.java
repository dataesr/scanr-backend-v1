/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.db.model;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * this class represents the lab from which a comapny is issued
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class SpinoffFrom {
    /**
     * id of the originating lab (RNSR id)
     */
    private String id;
    /**
     * Label of the originating lab
     */
    private String label;

    public String getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }
}
