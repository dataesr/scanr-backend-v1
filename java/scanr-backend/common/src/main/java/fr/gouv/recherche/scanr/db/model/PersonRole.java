/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */

package fr.gouv.recherche.scanr.db.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;

@ApiModel("v2.PersonRole")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class PersonRole {

	private String role;
	private String description;
	private Date startDate;
	private Date endDate;
	
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
	
}
