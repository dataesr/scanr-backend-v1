/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.db.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Represent different badges associated with a structure (hdr, anr, ...)
 * or any other root object.
 * code is used for indexation and generate image name.
 * label is used for display name.
 */
@ApiModel(value="v2.Badge",
	description="Represent different badges associated with a structure (hdr, anr, ...) or any other root object.")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Badge {
	@ApiModelProperty(value="used for indexation and generate image name", example="pcrdt")
    private String code;
    @ApiModelProperty("since v2 i18n")
    private I18nValue label;

    public Badge(String code, I18nValue label) {
        this.code = code;
        this.label = label;
    }

    public Badge() {
    }

    public String getCode() {
        return code;
    }
    public I18nValue getLabel() {
        return label;
    }

}
