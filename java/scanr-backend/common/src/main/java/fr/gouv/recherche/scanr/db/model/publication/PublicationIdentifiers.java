/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.db.model.publication;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.List;

/**
 * Stores the different identifiers of a publication
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class PublicationIdentifiers {
    private String doi;
    private String hal;
    private String prodinra;
    private String thesesfr;
    // The same publication can be published in different registrars (EP, WPO, US...)
    private List<String> patent;
    private List<String> oai;

    public void setDoi(String doi) {
        this.doi = doi;
    }

    public void setHal(String hal) {
        this.hal = hal;
    }

    public void setProdinra(String prodinra) {
        this.prodinra = prodinra;
    }

    public void setPatent(List<String> patent) {
        this.patent = patent;
    }

    public String getDoi() {
        return doi;
    }

    public String getHal() {
        return hal;
    }

    public String getProdinra() {
        return prodinra;
    }

    public List<String> getPatent() {
        return patent;
    }

    public List<String> getOai() {
        return oai;
    }

    public void setOai(List<String> oai) {
        this.oai = oai;
    }

    public String getThesesfr() {
        return thesesfr;
    }

    public void setThesesfr(String thesesfr) {
        this.thesesfr = thesesfr;
    }
}
