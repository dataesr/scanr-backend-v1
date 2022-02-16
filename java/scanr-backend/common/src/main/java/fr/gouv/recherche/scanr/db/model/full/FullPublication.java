/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.db.model.full;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.collect.Sets;
import fr.gouv.recherche.scanr.db.model.publication.Publication;
import io.swagger.annotations.ApiModel;

import java.util.Set;

/**
 * Full Publication: gather all the information (...) of a Publication.
 * Not in Publication mainly to save it separately in MongoDB.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@ApiModel("v2.FullPublication")
public class FullPublication extends Publication {

    // Fields
    public static final String FIELD_ID = "id";
    public static final String FIELD_TITLE = "title";
    public static final String FIELD_SUBTITLE = "subtitle";
    public static final String FIELD_SUMMARY = "summary";
    public static final String FIELD_ALTERNATIVE_SUMMARY = "alternativeSummary";
    public static final String FIELD_AFFILIATIONS = "affiliations";
    public static final String FIELD_AFFILIATIONS_ID = FIELD_AFFILIATIONS + ".id";
    public static final String FIELD_AFFILIATIONS_LABEL = FIELD_AFFILIATIONS + ".label";
    public static final String FIELD_AFFILIATIONS_ACRONYM = FIELD_AFFILIATIONS + ".acronym";
    public static final String FIELD_DOMAINS_LABEL = "domains.label";
    public static final String FIELD_DOMAINS_CODE = "domains.code";
    public static final String FIELD_SOURCE_TITLE = "source.title";
    public static final String FIELD_SOURCE_SUBTITLE = "source.subtitle";
    public static final String FIELD_SOURCE_ISSUE = "source.issue";
    public static final String FIELD_SOURCE_PUBLISHER = "source.publisher";
    public static final String FIELD_AUTHORS = "authors";
    public static final String FIELD_AUTHORS_FULLNAME = FIELD_AUTHORS + ".fullName";
    public static final String FIELD_AUTHORS_ROLE = FIELD_AUTHORS + ".role";
    public static final String FIELD_AUTHORS_PERSON_ID = FIELD_AUTHORS + ".person.id";
    public static final String FIELD_SUBMISSION_DATE = "submissionDate";
    public static final String FIELD_PUBLICATION_DATE = "publicationDate";
    public static final String FIELD_IS_OA = "isOa";
    public static final String FIELD_TYPE = "type";
    public static final String FIELD_AWARDS = "awards";
    public static final String FIELD_AWARDS_LABEL = FIELD_AWARDS + ".label";
    public static final String FIELD_AWARDS_STRUCTURENAME = FIELD_AWARDS + ".structureName";
    public static final String FIELD_PROJECTS_LABEL = "projects.label";
    public static final String FIELD_PROJECTS_ACRONYM = "projects.acronym";
    public static final String FIELD_PROJECTS_TITLE = "projects.title";
    public static final String FIELD_PROJECTS_ID = "projects.id";
    public static final String FIELD_PRODUCTIONTYPE = "productionType";
    public static final String FIELD_BADGES_CODE = "badges.code";
    public static final String FIELD_BADGES_LABEL = "badges.label";
    public static final String FIELD_EXTERNALIDS_ID = "externalIds.id";
    public static final String FIELD_LINKS_URL = "links.url";
    public static final String FIELD_KEYWORDS = "keywords";
    public static final String FIELD_IS_INTERNATIONAL = "isInternational";
    public static final String FIELD_IS_OEB = "isOeb";
    public static final String FIELD_GRANTED_DATE = "grantedDate";
    public static final String FIELD_PATENTS = "patents";
    public static final String FIELD_PATENTS_APPLICATION_NUMBER = FIELD_PATENTS + ".applicationNumber";
    public static final String FIELD_PATENTS_PUBLICATION_NUMBER = FIELD_PATENTS + ".publicationNumber";
    public static final String FIELD_LINKED_PRODUCTIONS = "linkedProductions";
    public static final String FIELD_LINKED_PRODUCTIONS_TITLE = FIELD_LINKED_PRODUCTIONS + ".title";

    @JsonIgnore
    private Set<FullPublicationField> fieldsToRefresh = Sets.newHashSet();

    public FullPublication(){}

    public FullPublication(String id) {
        this.setId(id);
    }

    public Set<FullPublicationField> getFieldsToRefresh() {
        return fieldsToRefresh;
    }

    public void setFieldsToRefresh(Set<FullPublicationField> fieldsToRefresh) {
        this.fieldsToRefresh = fieldsToRefresh;
    }

}
