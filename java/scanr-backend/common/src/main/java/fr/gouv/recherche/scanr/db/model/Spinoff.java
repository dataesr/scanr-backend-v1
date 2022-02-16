/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.db.model;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * This class represent a spinoff of an RNSR structure.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Spinoff {
    /**
     * Id of the project originating.
     */
    private String idProject;
    /**
     * id of the spin off company (SIREN)
     */
    private String idCompany;
    /**
     * type of the structure
     */
    private String type;
    /**
     * label of the company spinoff
     */
    private String labelCompany;
    /**
     * optional year when this spinoff has been closed
     */
    private Integer yearClosing;

    public String getIdProject() {
        return idProject;
    }

    public String getIdCompany() {
        return idCompany;
    }

    public String getType() {
        return type;
    }

    public String getLabelCompany() {
        return labelCompany;
    }

    public Integer getYearClosing() {
        return yearClosing;
    }
}
