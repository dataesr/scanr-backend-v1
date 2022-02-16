/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.db.model.publication;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonGetter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSetter;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Source of the publication
 */
@ApiModel("v2.PublicationSource")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class PublicationSource {
    @ApiModelProperty("Title of the source, may be null if part of a collection")
    private String title;
    private String subtitle;
    @ApiModelProperty("Type of the source, if COLLECTION then collection may contain collection data")
    private SourceType type; // comme avant (enum) plutôt que source_genre
    private Boolean isInDoaj; // since v2
    private Boolean isOa; // since v2
    @ApiModelProperty(value="pages information", example="512-518")
    private String pagination;
    @ApiModelProperty("Article number inside the source as complementary info")
    private String articleNumber;
    private String issue; // in v2 refactored from PublicationSourceCollection (Collection metadata aka. journal or revue or plenty of other names.)
    private String publisher; // since v2
    private List<String> journalIssns = new ArrayList<>(); // in v2 mutlivalued, refactored from PublicationSourceCollection (Collection metadata aka. journal or revue or plenty of other names)

	public enum SourceType {
        PROCEEDINGS, EVENT, BOOK, COLLECTION, ARTICLE
    }

    public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSubtitle() {
		return subtitle;
	}

	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}

	public SourceType getType() {
		return type;
	}

	public void setType(SourceType type) {
		this.type = type;
	}

	public Boolean getIsInDoaj() {
		return isInDoaj;
	}

	public void setIsInDoaj(Boolean isInDoaj) {
		this.isInDoaj = isInDoaj;
	}

	public Boolean getIsOa() {
		return isOa;
	}

	public void setIsOa(Boolean isOa) {
		this.isOa = isOa;
	}

	public String getPagination() {
		return pagination;
	}

	public void setPagination(String pagination) {
		this.pagination = pagination;
	}

	public String getArticleNumber() {
		return articleNumber;
	}

	public void setArticleNumber(String articleNumber) {
		this.articleNumber = articleNumber;
	}

	public String getIssue() {
		return issue;
	}

	public void setIssue(String issue) {
		this.issue = issue;
	}

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public List<String> getJournalIssns() {
		return journalIssns;
	}

	public void setJournalIssns(List<String> journalIssns) {
		this.journalIssns = journalIssns;
	}

}
