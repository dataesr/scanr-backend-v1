/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.db.model.full;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.collect.Sets;
import fr.gouv.recherche.scanr.db.model.Person;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;
import java.util.Set;

/**
 * Full Person: gather all the information (...) of a Person.
 * Denormalizes : (champs Light dans) coContributors, affiliations (et .structure en Light), employers, similarPersons,
 * et uniquement présent en Full : projects, publications.
 * Not in Person mainly to save it separately in MongoDB. 
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@ApiModel("v2.FullPerson")
public class FullPerson extends Person {

	// Fields
	public static final String FIELD_ID = "id";
	public static final String FIELD_FIRSTNAME = "firstName";
	public static final String FIELD_LASTNAME = "lastName";
	public static final String FIELD_MAIDEN_NAME = "maidenName";
	public static final String FIELD_FULLNAME = "fullName";
	public static final String FIELD_GENDER = "gender";
	public static final String FIELD_DOMAIN_LABEL = "domains.label";
	public static final String FIELD_KEYWORDS = "keywords";
	public static final String FIELD_DESCRIPTION = "description";
	public static final String FIELD_EXTERNALIDS_ID = "externalIds.id";
	public static final String FIELD_AFFILIATIONS_STRUCTURE = "affiliations.structure";
	public static final String FIELD_AFFILIATIONS_STRUCTURE_ID = FIELD_AFFILIATIONS_STRUCTURE + ".id";
	public static final String FIELD_AFFILIATIONS_STRUCTURE_LABEL = FIELD_AFFILIATIONS_STRUCTURE + ".label";
	public static final String FIELD_AFFILIATIONS_STRUCTURE_ACRONYM = FIELD_AFFILIATIONS_STRUCTURE + ".acronym";
	public static final String FIELD_AFFILIATIONS_STRUCTURE_ADDRESS = FIELD_AFFILIATIONS_STRUCTURE + ".address";
	public static final String FIELD_AFFILIATIONS_STRUCTURE_ADDRESS_CITY = FIELD_AFFILIATIONS_STRUCTURE_ADDRESS + ".city";
	public static final String FIELD_AFFILIATIONS_STRUCTURE_ADDRESS_CITYCODE = FIELD_AFFILIATIONS_STRUCTURE_ADDRESS + ".citycode";
	public static final String FIELD_AFFILIATIONS_STRUCTURE_ADDRESS_GPS = FIELD_AFFILIATIONS_STRUCTURE_ADDRESS + ".gps";
	public static final String FIELD_PUBLICATIONS_ID = "publications.publication.id";
	public static final String FIELD_PUBLICATIONS_TITLE = "publications.publication.title";
	public static final String FIELD_PUBLICATIONS_SUBTITLE = "publications.publication.subtitle";
	public static final String FIELD_PUBLICATIONS_SUMMARY = "publications.publication.summary";
	public static final String FIELD_PROJECTS_ID = "projects.project.id";
	public static final String FIELD_PROJECTS_LABEL = "projects.project.label";
	public static final String FIELD_PROJECTS_ACRONYME = "projects.project.acronyme";
	public static final String FIELD_WEBSITE = "website";
	public static final String FIELD_ROLES_DESCRIPTION = "roles.description";
	public static final String FIELD_LINKS_URL = "links.url";
	public static final String FIELD_BADGES_CODE = "badges.code";
	public static final String FIELD_BADGES_LABEL = "badges.label";
	public static final String FIELD_AWARDS_LABEL = "awards.label";
	public static final String FIELD_AWARDS_STRUCTURENAME = "awards.structureName";

	@ApiModelProperty("Only in Full. Champ alimenté par le champ \"persons\" présent dans le schéma \"Project\"")
	private List<FullPersonProject> projects;
	@ApiModelProperty("Only in Full. Champ alimenté par le champ \"authors\" présent dans le schéma \"Publication\", diffère de Person.affiliations")
	private List<FullPersonPublication> publications;

	@JsonIgnore
	private Set<FullPersonField> fieldsToRefresh = Sets.newHashSet();

	public FullPerson() {
	}

	public FullPerson(String id) {
		super(id);
	}

	public List<FullPersonProject> getProjects() {
		return projects;
	}
	public void setProjects(List<FullPersonProject> projects) {
		this.projects = projects;
	}
	public List<FullPersonPublication> getPublications() {
		return publications;
	}
	public void setPublications(List<FullPersonPublication> publications) {
		this.publications = publications;
	}

	public Set<FullPersonField> getFieldsToRefresh() {
		return fieldsToRefresh;
	}

	public void setFieldsToRefresh(Set<FullPersonField> fieldsToRefresh) {
		this.fieldsToRefresh = fieldsToRefresh;
	}

}
