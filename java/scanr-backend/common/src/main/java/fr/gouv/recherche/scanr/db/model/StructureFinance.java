/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.db.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Number associated with a research structure from RNSR.
 * It can store, nomber of employess, ratios..
 */
@ApiModel(value="v2.StructureFinance", description="Financial and statistical data about the structure")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class StructureFinance {
    @ApiModelProperty("Mere string (with currency ?)")
    private String revenue;
    @ApiModelProperty("Mere string (with currency ?), not used in aggregations")
    private String operatingIncome;
    @ApiModelProperty("Date of the information")
    private Date date;
    
	public String getRevenue() {
		return revenue;
	}
	public void setRevenue(String revenue) {
		this.revenue = revenue;
	}
	public String getOperatingIncome() {
		return operatingIncome;
	}
	public void setOperatingIncome(String operatingIncome) {
		this.operatingIncome = operatingIncome;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}

}
