/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */

package fr.gouv.recherche.scanr.db.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("v2.PersonEmployerRelation")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class PersonEmployerRelation {

	@ApiModelProperty("Light fields in Full objects, else only id")
	private Structure structure;
	// relation fields :
	private Date startDate;
	private Date endDate;
	// values of Structure fields as provided in the object that is source of the relation :
	private String label;
	
	public Structure getStructure() {
		return structure;
	}
	public void setStructure(Structure structure) {
		this.structure = structure;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	
}
