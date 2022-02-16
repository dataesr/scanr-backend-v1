/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.db.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * Outlinks domains
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class PerUrlOutDomain {
    private String url;
    private List<OutDomain> outDomains = Lists.newArrayList();

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<OutDomain> getOutDomains() {
        return outDomains;
    }

    public void setOutDomains(List<OutDomain> outDomains) {
        this.outDomains = outDomains;
    }
}
