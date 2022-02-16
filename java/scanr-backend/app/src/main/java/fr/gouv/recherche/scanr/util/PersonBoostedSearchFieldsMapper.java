/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */

package fr.gouv.recherche.scanr.util;

import fr.gouv.recherche.scanr.db.model.full.FullPerson;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class PersonBoostedSearchFieldsMapper implements BoostedSearchFieldsMapperInterface {

    public PersonBoostedSearchFieldsMapper() {}

    @Override
    public HashMap<String, Float> getBoostConfiguration() {
        HashMap<String, Float> boostConfiguration = new HashMap<>();

        boostConfiguration.putIfAbsent(FullPerson.FIELD_ID, 5f);
        boostConfiguration.putIfAbsent(FullPerson.FIELD_FULLNAME, 10f);
        boostConfiguration.putIfAbsent(FullPerson.FIELD_EXTERNALIDS_ID, 5f);
        boostConfiguration.putIfAbsent(FullPerson.FIELD_DOMAIN_LABEL, 2f);
        boostConfiguration.putIfAbsent(FullPerson.FIELD_AFFILIATIONS_STRUCTURE_LABEL, 2f);
        boostConfiguration.putIfAbsent(FullPerson.FIELD_AFFILIATIONS_STRUCTURE_ID, 2f);
        boostConfiguration.putIfAbsent(FullPerson.FIELD_AFFILIATIONS_STRUCTURE_ACRONYM, 2f);
        boostConfiguration.putIfAbsent(FullPerson.FIELD_KEYWORDS, 2f);
        boostConfiguration.putIfAbsent(FullPerson.FIELD_PUBLICATIONS_ID, 5f);
        boostConfiguration.putIfAbsent(FullPerson.FIELD_PUBLICATIONS_TITLE, 1f);
        boostConfiguration.putIfAbsent(FullPerson.FIELD_PUBLICATIONS_SUBTITLE, 1f);
        boostConfiguration.putIfAbsent(FullPerson.FIELD_PUBLICATIONS_SUMMARY, 1f);
        boostConfiguration.putIfAbsent(FullPerson.FIELD_PROJECTS_ID, 5f);
        boostConfiguration.putIfAbsent(FullPerson.FIELD_PROJECTS_LABEL, 1f);
        boostConfiguration.putIfAbsent(FullPerson.FIELD_PROJECTS_ACRONYME, 1f);
        boostConfiguration.putIfAbsent(FullPerson.FIELD_DESCRIPTION, 1f);
        boostConfiguration.putIfAbsent(FullPerson.FIELD_ROLES_DESCRIPTION, 1f);
        boostConfiguration.putIfAbsent(FullPerson.FIELD_BADGES_CODE, 2f);
        boostConfiguration.putIfAbsent(FullPerson.FIELD_BADGES_LABEL, 2f);
        boostConfiguration.putIfAbsent(FullPerson.FIELD_AWARDS_LABEL, 2f);
        boostConfiguration.putIfAbsent(FullPerson.FIELD_AWARDS_STRUCTURENAME, 2f);
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
