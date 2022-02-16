/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.search.model2.response;

import java.util.List;

public class FacetResult {

    private String id;
    private List<FacetResultEntry> entries;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<FacetResultEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<FacetResultEntry> entries) {
        this.entries = entries;
    }
}
