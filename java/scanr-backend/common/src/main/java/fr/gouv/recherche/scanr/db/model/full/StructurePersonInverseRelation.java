/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */

package fr.gouv.recherche.scanr.db.model.full;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import fr.gouv.recherche.scanr.db.model.Person;
import io.swagger.annotations.ApiModel;

@ApiModel(value="v2.StructurePersonInverseRelation", description="Inverse relation of Person.affiliations, in Full only")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class StructurePersonInverseRelation {
	
    private Person person;
    // original relation fields :
    private String role;
    private Date startDate;
    private Date endDate;
    private List<String> source;
    
	public Person getPerson() {
		return person;
	}
	public void setPerson(Person person) {
		this.person = person;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
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
	public List<String> getSource() {
		return source;
	}
	public void setSource(List<String> source) {
		this.source = source;
	}

}
