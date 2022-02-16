/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */

package fr.gouv.recherche.scanr.util;

import fr.gouv.recherche.scanr.db.model.full.FullPublication;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;

@Component
public class PublicationBoostedSearchFieldsMapper implements BoostedSearchFieldsMapperInterface {

    public PublicationBoostedSearchFieldsMapper() {}

    @Override
    public HashMap<String, Float> getBoostConfiguration() {
        HashMap<String, Float> boostConfiguration = new HashMap<>();

        boostConfiguration.putIfAbsent(FullPublication.FIELD_ID, 5f);
        boostConfiguration.putIfAbsent(FullPublication.FIELD_TITLE, 10f);
        boostConfiguration.putIfAbsent(FullPublication.FIELD_SUBTITLE, 8f);
        boostConfiguration.putIfAbsent(FullPublication.FIELD_SUMMARY, 8f);
        boostConfiguration.putIfAbsent(FullPublication.FIELD_ALTERNATIVE_SUMMARY, 8f);
        boostConfiguration.putIfAbsent(FullPublication.FIELD_AFFILIATIONS_LABEL, 5f);
        boostConfiguration.putIfAbsent(FullPublication.FIELD_SOURCE_TITLE, 1f);
        boostConfiguration.putIfAbsent(FullPublication.FIELD_SOURCE_SUBTITLE, 1f);
        boostConfiguration.putIfAbsent(FullPublication.FIELD_SOURCE_PUBLISHER, 1f);
        boostConfiguration.putIfAbsent(FullPublication.FIELD_PROJECTS_LABEL, 1f);
        boostConfiguration.putIfAbsent(FullPublication.FIELD_PROJECTS_ACRONYM, 1f);
        boostConfiguration.putIfAbsent(FullPublication.FIELD_AUTHORS_FULLNAME, 8f);
        boostConfiguration.putIfAbsent(FullPublication.FIELD_AUTHORS_PERSON_ID, 8f);
        boostConfiguration.putIfAbsent(FullPublication.FIELD_DOMAINS_LABEL, 1f);
        boostConfiguration.putIfAbsent(FullPublication.FIELD_DOMAINS_CODE, 1f);
        boostConfiguration.putIfAbsent(FullPublication.FIELD_AFFILIATIONS_ACRONYM, 3f);
        boostConfiguration.putIfAbsent(FullPublication.FIELD_AFFILIATIONS_ID, 8f);
        boostConfiguration.putIfAbsent(FullPublication.FIELD_BADGES_CODE, 2f);
        boostConfiguration.putIfAbsent(FullPublication.FIELD_BADGES_LABEL, 2f);
        boostConfiguration.putIfAbsent(FullPublication.FIELD_PROJECTS_TITLE, 5f);
        boostConfiguration.putIfAbsent(FullPublication.FIELD_PROJECTS_ID, 5f);
        boostConfiguration.putIfAbsent(FullPublication.FIELD_EXTERNALIDS_ID, 5f);
        boostConfiguration.putIfAbsent(FullPublication.FIELD_AWARDS_STRUCTURENAME, 2f);
        boostConfiguration.putIfAbsent(FullPublication.FIELD_AWARDS_LABEL, 2f);
        boostConfiguration.putIfAbsent(FullPublication.FIELD_LINKS_URL, 1f);
        boostConfiguration.putIfAbsent(FullPublication.FIELD_KEYWORDS, 5f);
        boostConfiguration.putIfAbsent(FullPublication.FIELD_PATENTS_APPLICATION_NUMBER, 10f);
        boostConfiguration.putIfAbsent(FullPublication.FIELD_PATENTS_PUBLICATION_NUMBER, 10f);
        boostConfiguration.putIfAbsent(FullPublication.FIELD_LINKED_PRODUCTIONS_TITLE, 3f);
        boostConfiguration.putIfAbsent(DEFAULT_KEY, DEFAULT_BOOST);

        return boostConfiguration;
    }

    @Override
    public Float getBoostForField(String fieldName) {
        HashMap<String, Float> boostConfiguration = this.getBoostConfiguration();

        if (boostConfiguration.containsKey(fieldName)) {
            return boostConfiguration.get(fieldName);
        } else return boostConfiguration.getOrDefault(DEFAULT_KEY, DEFAULT_BOOST);
    }
}
