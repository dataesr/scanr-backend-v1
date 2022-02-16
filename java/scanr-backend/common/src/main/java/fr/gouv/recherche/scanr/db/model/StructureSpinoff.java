/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.db.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 *
 */
@ApiModel(value="v2.StructureSpinoff", description="Spinoff of an RNSR structure")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class StructureSpinoff {
    @ApiModelProperty("Project originating, if known at least id")
    private Project project; // multivalued not necessary for the known data
    @ApiModelProperty("Spin off company (SIREN), if known at least id")
    private Structure structure;
    // relation fields :
    @ApiModelProperty("Type of the spinoff, most commonly a program")
    private String type; // toujours iLab ?!
    @ApiModelProperty("label of the company spinoff")
    private String label;
    @ApiModelProperty("optional year when this spinoff has been closed")
    private Integer yearClosing;

	public Project getProject() {
		return project;
	}
	public void setProject(Project project) {
		this.project = project;
	}
	public Structure getStructure() {
		return structure;
	}
	public void setStructure(Structure structure) {
		this.structure = structure;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public Integer getYearClosing() {
		return yearClosing;
	}
	public void setYearClosing(Integer yearClosing) {
		this.yearClosing = yearClosing;
	}

}
