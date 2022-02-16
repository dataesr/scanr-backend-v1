/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */

package fr.gouv.recherche.scanr.search.model2.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import fr.gouv.recherche.scanr.search.model2.request.LikeRequest;
import io.swagger.annotations.ApiModelProperty;

import java.util.Collection;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class LikeResponse<T> {

    @ApiModelProperty("Like request that provided this response's results")
    private LikeRequest request;
    private long total;
    @ApiModelProperty("Found objects")
    private Collection<SearchResult<T>> results;
    @ApiModelProperty("Computed buckets or bins for each named aggregation")
    private List<FacetResult> facets;

    public LikeResponse(LikeRequest request, long total, Collection<SearchResult<T>> results) {
        this.request = request;
        this.total = total;
        this.results = results;
    }

    public LikeRequest getRequest() {
        return request;
    }

    public void setRequest(LikeRequest request) {
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
