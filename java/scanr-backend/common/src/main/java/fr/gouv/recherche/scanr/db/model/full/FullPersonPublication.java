/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */

package fr.gouv.recherche.scanr.db.model.full;

import com.fasterxml.jackson.annotation.JsonInclude;
import fr.gouv.recherche.scanr.db.model.publication.Publication;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value="v2.FullPersonPublication", description="Inverse relation to Publication from FullPerson")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class FullPersonPublication {
	
	@ApiModelProperty("Light version of Publication")
	private Publication publication;
	// relation fields :
	private String role;
	// NB. publication.authors.affiliations is already brought in Publication's Light form
	
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

}
