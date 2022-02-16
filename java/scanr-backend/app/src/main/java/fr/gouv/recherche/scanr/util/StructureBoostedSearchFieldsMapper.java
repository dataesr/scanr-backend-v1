/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.util;

import fr.gouv.recherche.scanr.db.model.full.FullStructure;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;

@Component
public class StructureBoostedSearchFieldsMapper implements BoostedSearchFieldsMapperInterface {

    public StructureBoostedSearchFieldsMapper() {}

    @Override
    public HashMap<String, Float> getBoostConfiguration() {
        HashMap<String, Float> boostConfiguration = new HashMap<>();

        boostConfiguration.putIfAbsent(FullStructure.FIELDS.ACRONYM, 10f);
        boostConfiguration.putIfAbsent(FullStructure.FIELDS.INSTITUTIONS.CODE, 10f);
        boostConfiguration.putIfAbsent(FullStructure.FIELDS.ID, 5f);
        boostConfiguration.putIfAbsent(FullStructure.FIELDS.LABEL, 5f);
        boostConfiguration.putIfAbsent(FullStructure.FIELDS.PUBLICATIONS.ID, 5f);
        boostConfiguration.putIfAbsent(FullStructure.FIELDS.PROJECTS.ID, 5f);
        boostConfiguration.putIfAbsent(FullStructure.FIELDS.LEADERS.ID, 5f);
        boostConfiguration.putIfAbsent(FullStructure.FIELDS.EXTERNAL_IDS.ID, 5f);
        boostConfiguration.putIfAbsent(FullStructure.FIELDS.KEYWORDS, 3f);
        boostConfiguration.putIfAbsent(FullStructure.FIELDS.ALIAS, 2f);
        boostConfiguration.putIfAbsent(FullStructure.FIELDS.BADGES.CODE, 2f);
        boostConfiguration.putIfAbsent(FullStructure.FIELDS.BADGES.LABEL, 2f);
        boostConfiguration.putIfAbsent(FullStructure.FIELDS.DESCRIPTION, 2f);
        boostConfiguration.putIfAbsent(FullStructure.FIELDS.LEADERS.FULLNAME, 1f);
        boostConfiguration.putIfAbsent(FullStructure.FIELDS.ACTIVITIES.LABEL, 1f);
        boostConfiguration.putIfAbsent(FullStructure.FIELDS.PUBLICATIONS.TITLE, 1f);
        boostConfiguration.putIfAbsent(FullStructure.FIELDS.PUBLICATIONS.SUBTITLE, 1f);
        boostConfiguration.putIfAbsent(FullStructure.FIELDS.PUBLICATIONS.AUTHORS, 1f);
        boostConfiguration.putIfAbsent(FullStructure.FIELDS.PUBLICATIONS.SUMMARY, 1f);
        boostConfiguration.putIfAbsent(FullStructure.FIELDS.PUBLICATIONS.ALTERNATIVE_SUMMARY, 1f);
        boostConfiguration.putIfAbsent(FullStructure.FIELDS.PROJECTS.ACRONYM, 1f);
        boostConfiguration.putIfAbsent(FullStructure.FIELDS.PROJECTS.LABEL, 1f);
        boostConfiguration.putIfAbsent(FullStructure.FIELDS.PROJECTS.DESCRIPTION, 1f);
        boostConfiguration.putIfAbsent(FullStructure.FIELDS.WEBSITES.WEBPAGES.CONTENT, 1f);
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
