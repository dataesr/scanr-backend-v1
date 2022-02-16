/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */

package fr.gouv.recherche.scanr.search.model2.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.annotations.ApiModel;

@ApiModel("v2.AggregationTermsOrder") // for consistency
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AggregationTermsOrder {
	
	private SearchRequest.AggregationSortType type;
	private SearchRequest.SortDirection direction;

	public SearchRequest.AggregationSortType getType() {
		return type;
	}
	public void setType(SearchRequest.AggregationSortType type) {
		this.type = type;
	}
	public SearchRequest.SortDirection getDirection() {
		return direction;
	}
	public void setDirection(SearchRequest.SortDirection direction) {
		this.direction = direction;
	}
}
