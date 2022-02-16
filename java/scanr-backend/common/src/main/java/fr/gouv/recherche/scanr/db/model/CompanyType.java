/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.db.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;

/**
 * 
 */
@ApiModel(value="v2.CompanyType", description="Type of company (code/label)")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class CompanyType {
    private String id;
    private String label;

    public CompanyType() {
    }

    public CompanyType(String id, String label) {
        this.id = id;
        this.label = label;
    }

    public String getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

}
