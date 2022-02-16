/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */

package fr.gouv.recherche.scanr.db.model;

import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;

@ApiModel(value="v2.I18nValue", description="locale to translation dictionary / map, "
		+ "with \"default\" being the key of the value to be used if the translation "
		+ "in the user locale is not available.")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class I18nValue extends HashMap<String,String> {
	private static final long serialVersionUID = -8584378193767080213L;
	public static final String DEFAULT_LANGUAGE = "default"; 
}
