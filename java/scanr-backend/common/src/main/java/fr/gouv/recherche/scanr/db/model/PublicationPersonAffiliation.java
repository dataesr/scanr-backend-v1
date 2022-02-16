/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */

package fr.gouv.recherche.scanr.db.model;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


@ApiModel(value="v2.PublicationPersonAffiliation",
description="Affiliation relation of a publication's author. Used in Person")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class PublicationPersonAffiliation {
	@ApiModelProperty("The structure where the person is affiliated in if in scanr, only id outside Full object.")
    private Structure structure;
    // relation fields :
    private String role;
    private Date startDate;
    private Date endDate;
    private List<String> sources;
    @ApiModelProperty("Label brute de la structure fourni par MESRI")
    private String label; // and not structureName
    
	public Structure getStructure() {
		return structure;
	}
	public void setStructure(Structure structure) {
		this.structure = structure;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
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
	public List<String> getSources() {
		return sources;
	}
	public void setSources(List<String> sources) {
		this.sources = sources;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
}
