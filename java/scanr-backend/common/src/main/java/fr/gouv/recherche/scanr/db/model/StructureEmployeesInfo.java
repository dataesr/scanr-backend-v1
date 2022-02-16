/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.db.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value="v2.StructureEmployeesInfo", description="Information and statistics "
		+ "on employees of a structure (public or private)")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class StructureEmployeesInfo {
	
    private Integer employeesNb;
    @ApiModelProperty("Slice of the number of employees ex. 1000-1500, as a complete string field to be displayed as is in the interface")
    private String employeeNbSlice;
    @ApiModelProperty("enseignants-chercheurs nb, as a complete string field to be displayed as is in the interface")
    private String ecNb;
    @ApiModelProperty("number of HDR (habilité à diriger les recherches) person, as a complete string field to be displayed as is in the interface")
    private String hdrNb;
    @ApiModelProperty("Date of the information")
    private Date date;
    
	public Integer getEmployeesNb() {
		return employeesNb;
	}
	public void setEmployeesNb(Integer employeesNb) {
		this.employeesNb = employeesNb;
	}
	public String getEmployeeNbSlice() {
		return employeeNbSlice;
	}
	public void setEmployeeNbSlice(String employeeNbSlice) {
		this.employeeNbSlice = employeeNbSlice;
	}
	public String getEcNb() {
		return ecNb;
	}
	public void setEcNb(String ecNb) {
		this.ecNb = ecNb;
	}
	public String getHdrNb() {
		return hdrNb;
	}
	public void setHdrNb(String hdrNb) {
		this.hdrNb = hdrNb;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}

}
