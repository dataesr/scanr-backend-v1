/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */

package fr.gouv.recherche.scanr.db.model.full;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import fr.gouv.recherche.scanr.db.model.publication.Publication;
import io.swagger.annotations.ApiModel;

@ApiModel(value="v2.StructurePublicationInverseRelation", description="Inverse relation of Publication.authors.affiliations, in Full only")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class StructurePublicationInverseRelation {
	
    private Publication publication;
    // original relation fields (with Structure and Person) :
    private String role;
    private Date startDate;
    private Date endDate;
    private List<String> source;
    
	public Publication getPublication() {
		return publication;
	}
	public void setPublication(Publication publication) {
		this.publication = publication;
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
