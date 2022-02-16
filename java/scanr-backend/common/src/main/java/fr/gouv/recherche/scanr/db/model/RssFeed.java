/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
 
package fr.gouv.recherche.scanr.db.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;

/**
 * RSS feed detected by the core extractor.
 */
@ApiModel("v2.RssFeed")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RssFeed {

    private String url;
    private Float freq;

    public RssFeed() {
    }

    public RssFeed(String url, Float freq) {
        this.url = url;
        this.freq = freq;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Float getFreq() {
        return this.freq;
    }

    public void setFreq(Float freq) {
        this.freq = freq;
    }
}
