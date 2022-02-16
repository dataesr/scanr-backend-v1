/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */

package fr.gouv.recherche.scanr.db.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;

@ApiModel(value="v2.PublicationStructureAffiliation",
        description="Affiliation relation of a publication affiliation. Used in Publication")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class PublicationStructureAffiliation {

    private String structure;
    private String institutionLabel;
    private String label;
    private Address address;

    public PublicationStructureAffiliation() {
    }

    public PublicationStructureAffiliation(String structure) {
        this.structure = structure;
    }

    public String getStructure() {
        return structure;
    }

    public void setStructure(String structure) {
        this.structure = structure;
    }

    public String getInstitutionLabel() {
        return institutionLabel;
    }

    public void setInstitutionLabel(String institutionLabel) {
        this.institutionLabel = institutionLabel;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }
}
