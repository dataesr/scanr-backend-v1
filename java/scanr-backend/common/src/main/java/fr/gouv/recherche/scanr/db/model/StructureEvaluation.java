/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */

package fr.gouv.recherche.scanr.db.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("v2.StructureEvaluation")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class StructureEvaluation {

	@ApiModelProperty("entité mais pas Structure ScanESR")
	private String evaluator;
	private String url;
	@ApiModelProperty("2016")
	private Integer year;
	
	public String getEvaluator() {
		return evaluator;
	}
	public void setEvaluator(String evaluator) {
		this.evaluator = evaluator;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public Integer getYear() {
		return year;
	}
	public void setYear(Integer year) {
		this.year = year;
	}
	
}
