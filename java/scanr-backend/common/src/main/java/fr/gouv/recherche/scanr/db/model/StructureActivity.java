/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.db.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Represent different types of Structure activity descriptions.
 * <ul>
 * <li>
 * NAF
 * </li>
 * <li>
 * ERC - (secondary are possible)
 * </li>
 * <li>
 * Domains
 * </li>
 * <li>
 * (nothing) Champs libres
 * </li>
 * </ul>
 * <p>
 * Indexation:
 * <ul>
 * <li> for coded nomenclatures (NAF, ERC, Domains), use a facet by Type</li>
 * <li> for all nomenclatures stores the labels</li>
 * </ul>
 */
@ApiModel("v2.StructureActivity")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class StructureActivity {
    private static final Logger log = LoggerFactory.getLogger(StructureActivity.class);
    /**
     * code of the activity
     */
    private String code;
    @ApiModelProperty("ex. NAF / Domaine scientifique / Discipline ERC / Thème, before v2 was \"type\" and Enum")
    private String type; // and not ENUM : NAF / Domaine scientifique / Discipline ERC / Thème
    /**
     * lable of the activity
     */
    private I18nValue label;
    @ApiModelProperty("true for secondary activities")
    private Boolean secondary;
    
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public I18nValue getLabel() {
		return label;
	}
	public void setLabel(I18nValue label) {
		this.label = label;
	}
	public Boolean getSecondary() {
		return secondary;
	}
	public void setSecondary(Boolean secondary) {
		this.secondary = secondary;
	}
	public static Logger getLog() {
		return log;
	}
}
