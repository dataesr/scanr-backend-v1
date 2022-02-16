/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.db.model.publication;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Collection of the publication.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class PublicationSourceCollection {
    /**
     * Collection title
     */
    private String title;
    /**
     * ISSN number
     */
    private String issn;
    /**
     * The number of the issue, this is typically of the format "9 (1)" (first article of the 9th volume)
     */
    private String issue;

    public PublicationSourceCollection() {
    }

    public PublicationSourceCollection(String title, String issn, String issue) {
        this.title = title;
        this.issn = issn;
        this.issue = issue;
    }

    public String getTitle() {
        return title;
    }

    public String getIssn() {
        return issn;
    }

    public String getIssue() {
        return issue;
    }
}
