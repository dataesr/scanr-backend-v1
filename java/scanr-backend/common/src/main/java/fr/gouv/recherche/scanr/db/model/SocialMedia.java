/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */

package fr.gouv.recherche.scanr.db.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * TODO unify with SocialAccount !?
 * @author mdutoo
 *
 */
@ApiModel("v2.SocialMedia")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class SocialMedia {
	
	private String account;
	private String type;
	private String url;
	@ApiModelProperty(value="locale (en, fr) of the account", example="fr")
	private String language;
	
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}

}
