/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.db.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSetter;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 
 */
@ApiModel(value="v2.StructureParentRelation", description="Parent structure reference. "
		+ "In v2 renamed from ParentReference")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class StructureParentRelation {
    @ApiModelProperty("Parent structure, at least id")
    private Structure structure;
    // relation fields :
    private Date fromDate;
    @ApiModelProperty("Type of parent relation (including whether exclusive)")
    private String relationType;
    // value of parent structure fields as provided in the child structure :
    @ApiModelProperty("if id not known")
    private String label;
    // NB. "exclusive" (Is this structure hierarchy exclusive, true by default) removed, will be in type
    

    public StructureParentRelation() {
    }

	public Structure getStructure() {
		return structure;
	}

	public void setStructure(Structure structure) {
		this.structure = structure;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public String getRelationType() {
		return relationType;
	}

	public void setRelationType(String relationType) {
		this.relationType = relationType;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
    
}
