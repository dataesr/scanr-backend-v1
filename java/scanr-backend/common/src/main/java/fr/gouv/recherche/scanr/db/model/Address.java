/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.db.model;


import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fr.gouv.recherche.scanr.util.GeoDeserializer;
import fr.gouv.recherche.scanr.util.GeoSerializer;
import org.elasticsearch.common.geo.GeoPoint;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


@ApiModel("v2.Address")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Address {
	@ApiModelProperty(value="Whether this address is the main one of the object, false by default. Since v2")
	private boolean isMain = false;
	@ApiModelProperty("Anything not in other fields, especially city and country.")
    private String address;
    private String postcode;
    @ApiModelProperty(example="Paris")
    private String city;
    @ApiModelProperty(example="75009")
    private String citycode;
	@ApiModelProperty("since v2")
    private String country;
	@ApiModelProperty("Address information provider. Since v2")
    private String provider;
	@ApiModelProperty("Score relatif au provider des informations liées à l'adresse. Since v2")
    private String score; // NB. ne pas être float empêcherait des ex. agrégations numériques mais pas nécessaire ici
	@ApiModelProperty("Geo position, in Elasticsearch GeoPoint (lat, lon). "
			+ "NB. in v1, was Spring Data Mongo Point (longitude, latitude), "
			+ "so no more need for specific Mongo deserialization or indexing")
	@JsonDeserialize(using = GeoDeserializer.class)
	@JsonSerialize(using = GeoSerializer.class)
    private GeoPoint gps;
    @ApiModelProperty("Provided to OpenStreetMap ?")
    private String urbanUnitCode;
    @ApiModelProperty("Provided to OpenStreetMap for display ?")
    private String urbanUnitLabel;
    @ApiModelProperty("Gathers addres, city... fields to provide autocompletion in a single \"place\" "
    		+ "input using Elasticsearch suggest API. Computed by ScanESR on all Address instances.")
    private List<String> localisationSuggestions;

    public Address() {
    }

	public boolean isMain() {
		return isMain;
	}

	public void setMain(boolean isMain) {
		this.isMain = isMain;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPostcode() {
		return postcode;
	}

	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCitycode() {
		return citycode;
	}

	public void setCitycode(String citycode) {
		this.citycode = citycode;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	public String getScore() {
		return score;
	}

	public void setScore(String score) {
		this.score = score;
	}

	public GeoPoint getGps() {
		return gps;
	}

	public void setGps(GeoPoint gps) {
		this.gps = gps;
	}

	public String getUrbanUnitCode() {
		return urbanUnitCode;
	}

	public void setUrbanUnitCode(String urbanUnitCode) {
		this.urbanUnitCode = urbanUnitCode;
	}

	public String getUrbanUnitLabel() {
		return urbanUnitLabel;
	}

	public void setUrbanUnitLabel(String urbanUnitLabel) {
		this.urbanUnitLabel = urbanUnitLabel;
	}

	public List<String> getLocalisationSuggestions() {
		return localisationSuggestions;
	}

	public void setLocalisationSuggestions(List<String> localisationSuggestions) {
		this.localisationSuggestions = localisationSuggestions;
	}
}
