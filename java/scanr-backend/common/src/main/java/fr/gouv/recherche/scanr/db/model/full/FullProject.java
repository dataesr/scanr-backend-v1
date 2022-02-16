/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.db.model.full;

import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import com.google.common.collect.Sets;
import fr.gouv.recherche.scanr.db.model.I18nValue;
import fr.gouv.recherche.scanr.db.model.Project;
import fr.gouv.recherche.scanr.db.model.publication.Publication;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Full Project: gather all the information (...) of a Project.
 * Not in Project mainly to save it separately in MongoDB. 
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@ApiModel("v2.FullProject")
public class FullProject extends Project {

	// Fields
	public static final String FIELD_ID = "id";
	public static final String FIELD_TITLE = "label";// champ inconnu
	public static final String FIELD_SUBTITLE = "id";// champ inconnu
	public static final String FIELD_TYPE = "type";
	public static final String FIELD_START_DATE = "startDate";
	public static final String FIELD_END_DATE = "endDate";
	public static final String FIELD_YEAR = "year";
	public static final String FIELD_BUDGET_FINANCED = "budgetFinanced";
	public static final String FIELD_BUDGET_TOTAL = "budgetTotal";
	public static final String FIELD_SUMMARY = "id";// champ inconnu
	public static final String FIELD_DOMAIN_LABEL = "domains.label";
	public static final String FIELD_STRUCTURE = "participants.structure";
	public static final String FIELD_STRUCTURE_LABEL = "participants.structure.label";
	public static final String FIELD_STRUCTURE_ACRONYM = "participants.structure.acronym";
	public static final String FIELD_STRUCTURE_ADDRESS_GPS = "participants.structure.address.gps";
	public static final String FIELD_URL = "url";
	public static final String FIELD_PROJECT_URL = "projectUrl";
	public static final String FIELD_PARTICIPANT_COUNT = "participantCount";
	public static final String FIELD_CALL_LABEL = "call.label";
	public static final String FIELD_ACTION_LABEL = "action.label";
	public static final String FIELD_DURATION = "duration";
	public static final String FIELD_PUBLICATION_TITLE = "publications.title";
	public static final String FIELD_PUBLICATION_SUBTITLE = "publications.subtitle";
	public static final String FIELD_PUBLICATION_SUMMARY = "publications.summary";
	public static final String FIELD_PERSONS_FULLNAME = "persons.fullName";
	public static final String FIELD_PERSONS_ID = "persons.id";
	public static final String FIELD_ACRONYM = "acronym";
	public static final String FIELD_DESCRIPTION = "description";
	public static final String FIELD_KEYWORDS = "keywords";
	public static final String FIELD_CALL_ID = "call.id";
	public static final String FIELD_BADGES_CODE = "badges.code";
	public static final String FIELD_BADGES_LABEL = "badges.label";
	public static final String FIELD_ACTION_ID = "action.id";
	public static final String FIELD_PARTICIPANTS_STRUCTURE_ID = "participants.structure.id";
	public static final String FIELD_PUBLICATIONS_ID = "publications.id";

	@ApiModelProperty("Only in Full. Champ alimenté par le champ \"projects\" présent dans le schéma \"Publication\" (recherche des publications liées au projet)")
	private List<Publication> publications; // no relation meta

	@JsonIgnore
	private Set<FullProjectField> fieldsToRefresh = Sets.newHashSet();

	public FullProject() {}

	public FullProject(String id) {
		super(id);
	}

	public FullProject(String id, String type, I18nValue acronym, I18nValue label) {
		super(id, type, acronym, label);
	}

	public List<Publication> getPublications() {
		return publications;
	}

	public void setPublications(List<Publication> publications) {
		this.publications = publications;
	}

	public Set<FullProjectField> getFieldsToRefresh() {
		return fieldsToRefresh;
	}

	public void setFieldsToRefresh(Set<FullProjectField> fieldsToRefresh) {
		this.fieldsToRefresh = fieldsToRefresh;
	}

}
