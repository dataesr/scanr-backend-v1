/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.db.model.full;

import com.fasterxml.jackson.annotation.JsonInclude;
import fr.gouv.recherche.scanr.db.model.I18nValue;

/**
 * External structure referenced in a project
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ExternalStructure {
    private I18nValue label;

    private String url;


    public ExternalStructure(I18nValue label, String url) {
        this.label = label;
        this.url = url;
    }

    public I18nValue getLabel() {
        return label;
    }

    public String getUrl() {
        return url;
    }
}
