/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.db.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Structure type (company, public private...)
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class StructureType {
    public static final StructureType RNSR_STRUCTURE = new StructureType("rnsr", "RNSR", true);
    private String id;
    private String label;
    @JsonProperty("isPublic")
    private boolean publicEntity = false;

    public StructureType() {
    }

    public StructureType(String id, String label, boolean publicEntity) {
        this.id = id;
        this.label = label;
        this.publicEntity = publicEntity;
    }

    public String getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public boolean isPublicEntity() {
        return publicEntity;
    }

    public void setPublicEntity(boolean publicEntity) {
        this.publicEntity = publicEntity;
    }
}
