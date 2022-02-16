/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */

package fr.gouv.recherche.scanr.search.model2.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Map;

@ApiModel("v2.Aggregation")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Aggregation {

    @ApiModelProperty("Name of the field to aggregate on")
    private String field;

    @ApiModelProperty("In the case of a \"filters\" aggregation : named filters, each defining a bucket or bin, "
            + "see https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations-bucket-filters-aggregation.html)")
    private Map<String, SearchFilter> filters;
    public SearchFilter filtersMapValueTypeDummy;

    @ApiModelProperty("In the case of a \"terms\" aggregation (each value defining dynamically a separate bucket or bin), "
            + "see https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations-bucket-terms-aggregation.html)")
    private AggregationTermsOrder order;
    @ApiModelProperty("In the case of a \"terms\" aggregation (each value defining dynamically a separate bucket or bin), "
            + "see https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations-bucket-terms-aggregation.html)")
    private Integer size;
    @ApiModelProperty("In the case of a \"terms\" aggregation (each value defining dynamically a separate bucket or bin), "
            + "see https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations-bucket-terms-aggregation.html)")
    private Integer min_doc_count;

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public Map<String, SearchFilter> getFilters() {
        return filters;
    }

    public void setFilters(Map<String, SearchFilter> filters) {
        this.filters = filters;
    }

    public AggregationTermsOrder getOrder() {
        return order;
    }

    public void setOrder(AggregationTermsOrder order) {
        this.order = order;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Integer getMin_doc_count() {
        return min_doc_count;
    }

    public void setMin_doc_count(Integer min_doc_count) {
        this.min_doc_count = min_doc_count;
    }
}
