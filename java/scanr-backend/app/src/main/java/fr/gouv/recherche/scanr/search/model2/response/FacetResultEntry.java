/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.search.model2.response;

import java.util.ArrayList;
import java.util.List;

public class FacetResultEntry {

    private String value;
    private Long count;
    private List<FacetResult> subFacets = new ArrayList<>();

    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }
    public Long getCount() {
        return count;
    }
    public void setCount(Long count) {
        this.count = count;
    }
    public List<FacetResult> getSubFacets() {
        return subFacets;
    }
    public void setSubFacets(List<FacetResult> subFacets) {
        this.subFacets = subFacets;
    }
    public void addSubFacet(FacetResult subFacet) {
        this.subFacets.add(subFacet);
    }
}
