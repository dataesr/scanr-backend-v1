/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */

package fr.gouv.recherche.scanr.search.model2.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import fr.gouv.recherche.scanr.search.model2.request.SearchRequest;
import io.swagger.annotations.ApiModelProperty;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * "Full" ? used in used in (advanced) /structure/search
 */
//no need for Swagger API, definition is embedded
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SearchResponse<T> {

    @ApiModelProperty("Search request that provided this response's results")
    private SearchRequest request;
    private long total;
    @ApiModelProperty("Found objects")
    private Collection<SearchResult<T>> results;
    @ApiModelProperty("Computed buckets or bins for each named aggregation")
    private List<FacetResult> facets;
    public FacetResult histogramsMapValueTypeDummy;

    public SearchResponse(SearchRequest request) {
        this.request = request;
    }

    public SearchResponse(SearchRequest request, long total, Collection<SearchResult<T>> results) {
        this.request = request;
        this.total = total;
        this.results = results;
    }

    public SearchRequest getRequest() {
        return request;
    }

    public void setRequest(SearchRequest request) {
        this.request = request;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public Collection<SearchResult<T>> getResults() {
        return results;
    }

    public void setResults(Collection<SearchResult<T>> results) {
        this.results = results;
    }

    public List<FacetResult> getFacets() {
        return facets;
    }

    public void setFacets(List<FacetResult> facets) {
        this.facets = facets;
    }
}
