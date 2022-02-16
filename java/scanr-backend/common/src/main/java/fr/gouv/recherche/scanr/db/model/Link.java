/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.db.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Link for documentatio of a structure
 */
@ApiModel("v2.Link")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Link {

    public static String MAIN_TYPE = "main";

    /**
     * TODO remove save if require by scanesr (computed)
     */
    private String id;

    @ApiModelProperty("Not Enum since v2, ex. main, repository, team, personal, wikipedia, hceres, rnsr")
    private String type;

    /**
     * url of the link
     */
    private String url;

    /**
     * label to be displayed
     */
    private String label;

    /**
     * crawl mode for links to be crawled (websites of structures). Still in v2.
     */
    private CrawlMode mode;

    public Link(String type, String url) {
        this.type = type;
        this.url = url;
        if (url != null)
            id = Website.idFromUrl(url);
    }

    public Link() {
    }

    public String getType() {
        return type;
    }

    public String getUrl() {
        return url;
    }

    public String getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public void computeId() {
        if (url == null) return;
        try {
            id = Website.idFromUrl(url);
        } catch (IllegalArgumentException ignored) {
            // id will stay null as url is not parsable...
            id = null;
        }
    }

    public CrawlMode getMode() {
        return mode;
    }

    public void setMode(CrawlMode mode) {
        this.mode = mode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Link link = (Link) o;
        return Objects.equals(id, link.id) &&
                type == link.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type);
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
