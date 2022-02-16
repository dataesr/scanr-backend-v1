/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */

package fr.gouv.recherche.scanr.util;

import fr.gouv.recherche.scanr.db.model.full.FullProject;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;

@Component
public class ProjectBoostedSearchFieldsMapper implements BoostedSearchFieldsMapperInterface {

    public ProjectBoostedSearchFieldsMapper() {}

    @Override
    public HashMap<String, Float> getBoostConfiguration() {
        HashMap<String, Float> boostConfiguration = new HashMap<>();

        boostConfiguration.putIfAbsent(FullProject.FIELD_ID, 5f);
        boostConfiguration.putIfAbsent(FullProject.FIELD_TITLE, 10f);
        boostConfiguration.putIfAbsent(FullProject.FIELD_DOMAIN_LABEL, 2f);
        boostConfiguration.putIfAbsent(FullProject.FIELD_STRUCTURE_LABEL, 2f);
        boostConfiguration.putIfAbsent(FullProject.FIELD_STRUCTURE_ACRONYM, 2f);
        boostConfiguration.putIfAbsent(FullProject.FIELD_PUBLICATION_TITLE, 1f);
        boostConfiguration.putIfAbsent(FullProject.FIELD_PUBLICATION_SUBTITLE, 1f);
        boostConfiguration.putIfAbsent(FullProject.FIELD_PUBLICATION_SUMMARY, 1f);
        boostConfiguration.putIfAbsent(FullProject.FIELD_CALL_LABEL, 1f);
        boostConfiguration.putIfAbsent(FullProject.FIELD_PERSONS_FULLNAME, 5f);
        boostConfiguration.putIfAbsent(FullProject.FIELD_PERSONS_ID, 5f);
        boostConfiguration.putIfAbsent(FullProject.FIELD_ACRONYM, 8f);
        boostConfiguration.putIfAbsent(FullProject.FIELD_DESCRIPTION, 3f);
        boostConfiguration.putIfAbsent(FullProject.FIELD_KEYWORDS, 2f);
        boostConfiguration.putIfAbsent(FullProject.FIELD_CALL_ID, 5f);
        boostConfiguration.putIfAbsent(FullProject.FIELD_BADGES_CODE, 2f);
        boostConfiguration.putIfAbsent(FullProject.FIELD_BADGES_LABEL, 2f);
        boostConfiguration.putIfAbsent(FullProject.FIELD_ACTION_ID, 5f);
        boostConfiguration.putIfAbsent(FullProject.FIELD_PARTICIPANTS_STRUCTURE_ID, 2f);
        boostConfiguration.putIfAbsent(FullProject.FIELD_PUBLICATIONS_ID, 3f);
        boostConfiguration.putIfAbsent(FullProject.FIELD_ACTION_LABEL, 1f);
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
