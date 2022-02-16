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
@ApiModel(value="v2.StructureRelation", description="Relation between structures.")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class StructureRelation {
	
	@ApiModelProperty(value="Related Structure if known in DB, at least identifier", example="ED 217")
    private Structure structure;
	// relation fields :
	private Date fromDate;
    @ApiModelProperty("Type of the target structure (type of the relation). Unconstrained since v2, "
    		+ "before : Comue / Carnot /Ecole Doctorale / Pôle de compétitivité /Incubateur")
    private String type;
    
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
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
}
