/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.search.model2.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * used in (advanced) /structure/search
 * This class is used to add score and highlights to the results
 */
// no need for Swagger API, definition is embedded
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SearchResult<T> {
    private List<HighlightItem> highlights;
    @ApiModelProperty("TODO float as in Elastisearch ?")
    private Object score;
    private T value;

    public SearchResult() {
    }

    public SearchResult(T value) {
        this.value = value;
    }

    public List<HighlightItem> getHighlights() {
        return highlights;
    }

    public void setHighlights(List<HighlightItem> highlights) {
        this.highlights = highlights;
    }

    public Object getScore() {
        return this.score;
    }

    public void setScore(Object score) {
        this.score = score;
    }

    public T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
	}

	public static class HighlightItem {
        public String type;
        public String value;

        public HighlightItem(String type, String value) {
            this.type = type;
            this.value = value;
        }
    }
}
