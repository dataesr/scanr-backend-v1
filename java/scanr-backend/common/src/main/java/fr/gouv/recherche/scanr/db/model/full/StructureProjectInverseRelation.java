/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */

package fr.gouv.recherche.scanr.db.model.full;

import com.fasterxml.jackson.annotation.JsonInclude;
import fr.gouv.recherche.scanr.db.model.Project;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value="v2.StructureProjectInverseRelation", description="Inverse relation of Project.participants, in Full only")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class StructureProjectInverseRelation {
	
    private Project project;
    // original relation fields :
    @ApiModelProperty("Funding amount in the project")
    private String funding;
    private String role;
    
	public Project getProject() {
		return project;
	}
	public void setProject(Project project) {
		this.project = project;
	}
	public String getFunding() {
		return funding;
	}
	public void setFunding(String funding) {
		this.funding = funding;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}

}
