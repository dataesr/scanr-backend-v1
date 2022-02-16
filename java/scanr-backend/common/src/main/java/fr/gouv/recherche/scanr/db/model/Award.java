/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */

package fr.gouv.recherche.scanr.db.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value="v2.Award", description="Used in Person (all fields), Publication (only label, date)."
		+ "En Light uniquement label (et non prize), date.")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Award {
	
	private String label;
	private Date date;
	private String url;
	private String description;
	@ApiModelProperty("including currency")
	private String amount; // dont monnaie (donc pas float, sinon monnaie dans un autre champ description)
	@ApiModelProperty("Label brut de l'institution liée, fourni par MESRI")
	private String structureName;
	@ApiModelProperty("in Light version when included in a Full object")
	private Structure structure;
	
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getStructureName() {
		return structureName;
	}
	public void setStructureName(String structureName) {
		this.structureName = structureName;
	}
	public Structure getStructure() {
		return structure;
	}
	public void setStructure(Structure structure) {
		this.structure = structure;
	}
	
}
