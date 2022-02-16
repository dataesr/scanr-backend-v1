/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */

package fr.gouv.recherche.scanr.db.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;

@ApiModel("v2.ProjectAction")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ProjectAction {
	
	private String id;
	private I18nValue label;
	private String level; // not integer nor Enum
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public I18nValue getLabel() {
		return label;
	}
	public void setLabel(I18nValue label) {
		this.label = label;
	}
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}

}
