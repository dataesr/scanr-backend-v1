/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.db.model;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Structures implicated in a project.
 * It can be either
 * <ul>
 * <li>an identified structure and the id (siren or RNSR structure) is provided</li>
 * <li>an external structure, id is null but label and url is provided</li>
 * </ul>
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ProjectStructure {
    /**
     * scanr id of the project structure
     */
    private String id;
    /**
     * label of the project structure if this structure is not in scanr
     */
    private String label;
    /**
     * url of the project structure if this structure is not in scanr
     */
    private String url;

    public ProjectStructure() {
    }

    public ProjectStructure(String id, String label, String url) {
        this.id = id;
        this.label = label;
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public String getUrl() {
        return url;
    }

}
