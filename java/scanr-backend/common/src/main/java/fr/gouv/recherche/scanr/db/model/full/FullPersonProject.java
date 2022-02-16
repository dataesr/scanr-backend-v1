/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */

package fr.gouv.recherche.scanr.db.model.full;

import com.fasterxml.jackson.annotation.JsonInclude;
import fr.gouv.recherche.scanr.db.model.Project;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value="v2.FullPersonProject", description="Inverse relation to project from FullPerson")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class FullPersonProject {
	
	@ApiModelProperty("Light version of Project")
	private Project project;
	// relation fields :
	private String role;
	
	public Project getProject() {
		return project;
	}
	public void setProject(Project project) {
		this.project = project;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}

}
