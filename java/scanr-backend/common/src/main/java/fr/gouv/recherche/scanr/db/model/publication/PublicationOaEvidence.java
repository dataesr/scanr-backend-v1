/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */

package fr.gouv.recherche.scanr.db.model.publication;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;

@ApiModel("v2.PublicationOaEvidence")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class PublicationOaEvidence {
	private String hostType;
	private String version;
	private Date updated;
	private String url;
	private String pdfUrl;
	private String landingPageUrl;
	private String license;
	
	public String getHostType() {
		return hostType;
	}
	public void setHostType(String hostType) {
		this.hostType = hostType;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public Date getUpdated() {
		return updated;
	}
	public void setUpdated(Date updated) {
		this.updated = updated;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getPdfUrl() {
		return pdfUrl;
	}
	public void setPdfUrl(String pdfUrl) {
		this.pdfUrl = pdfUrl;
	}
	public String getLandingPageUrl() {
		return landingPageUrl;
	}
	public void setLandingPageUrl(String landingPageUrl) {
		this.landingPageUrl = landingPageUrl;
	}
	public String getLicense() {
		return license;
	}
	public void setLicense(String license) {
		this.license = license;
	}
	
}
