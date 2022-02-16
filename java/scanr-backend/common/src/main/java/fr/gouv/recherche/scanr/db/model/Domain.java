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
@ApiModel(value="v2.Domain", description="Domain of research of Structure and its offers, Person, Publication")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Domain {
	@ApiModelProperty("code interne du domaine")
    private String code;
    @ApiModelProperty("since v2 i18n")
    private I18nValue label;
    @ApiModelProperty("not constrained")
    private String type;
    private String score; // Nouveau champ, pas nécessairement float
    private String url;
    private String level; // pas (nécessairement) numérique (label du level ?), pas Enum
    
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public I18nValue getLabel() {
		return label;
	}
	public void setLabel(I18nValue label) {
		this.label = label;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getScore() {
		return score;
	}
	public void setScore(String score) {
		this.score = score;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
    
}
