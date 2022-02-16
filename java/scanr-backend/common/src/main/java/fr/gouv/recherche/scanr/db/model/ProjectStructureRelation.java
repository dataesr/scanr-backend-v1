/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.db.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Relation of Structures that are members in a Project.
 * It can be either
 * <ul>
 * <li>an identified structure and the id (siren or RNSR structure) is provided</li>
 * <li>an external structure, id is null but label and url is provided</li>
 * </ul>
 */
@ApiModel(value="v2.ProjectStructureRelation", description="Relation of Structures that are members in a Project.")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ProjectStructureRelation {
	
	@ApiModelProperty("in FullProject also Light fields, else only id")
    private Structure structure;
	
	// values of Structure fields as provided in the object that is source of the relation :
    @ApiModelProperty("label of the project structure if this structure is not in scanr")
    private I18nValue label;
    @ApiModelProperty("url of the project structure if this structure is not in scanr")
    private String url;
    
    // relation fields :
    @ApiModelProperty("Funding amount in the project")
    private String funding;
    private String role;

	public Structure getStructure() {
		return structure;
	}

	public void setStructure(Structure structure) {
		this.structure = structure;
	}

	public I18nValue getLabel() {
		return label;
	}

	public void setLabel(I18nValue label) {
		this.label = label;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
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
