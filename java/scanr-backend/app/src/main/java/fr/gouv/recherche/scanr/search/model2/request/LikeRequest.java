/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
 
package fr.gouv.recherche.scanr.search.model2.request;

import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/** used in used in (advanced) /x/search */
@ApiModel("v2.LikeRequest") // for consistency
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class LikeRequest {

	private static final int PAGE_SIZE = 10;
	private static final int MAX_PAGE = 100;

    @ApiModelProperty("Language ex. \"fr\", before v2 was not used")
    private String lang; // not Locale because 10+ fields in JSON !

    @ApiModelProperty("Fields where to look up for similarity in")
    private List<String> fields;

    @ApiModelProperty("Ids of objects for results to be similar to")
    private List<String> likeIds;
    @ApiModelProperty("Texts for results to be similar to")
    private List<String> likeTexts;

	@ApiModelProperty("Page")
	private int page = 0;

	@ApiModelProperty("Number of results by page")
	private int pageSize = PAGE_SIZE;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LikeRequest that = (LikeRequest) o;
        return Objects.equals(lang, that.lang) &&
                Objects.equals(fields, that.fields) &&
                Objects.equals(likeIds, that.likeIds) &&
                Objects.equals(likeTexts, that.likeTexts) &&
				Objects.equals(page, that.page) &&
				Objects.equals(pageSize, that.pageSize);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lang, fields, likeIds, likeTexts, page, pageSize);
    }

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public List<String> getFields() {
		return fields;
	}

	public void setFields(List<String> fields) {
		this.fields = fields;
	}

	public List<String> getLikeIds() {
		return likeIds;
	}

	public void setLikeIds(List<String> likeIds) {
		this.likeIds = likeIds;
	}

	public List<String> getLikeTexts() {
		return likeTexts;
	}

	public void setLikeTexts(List<String> likeTexts) {
		this.likeTexts = likeTexts;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
}
