/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.search.model2.response;

import org.elasticsearch.search.aggregations.bucket.MultiBucketsAggregation.Bucket;

public enum FacetEnum {
    ACTION_LABELS,
    AFFILIATION_STRUCTURE_LABEL,
    AFFILIATION_STRUCTURE_ID,
    AWARDS,
    BADGES,
    BUDGET_FINANCED,
    BUDGET_TOTAL,
    CALLS,
    CALL_LABELS,
    DEPARTMENTS,
    END_DATES,
    INSTITUTIONS,
    KIND,
    LEVEL,
    LOCALISATIONS,
    NAF,
    NATURES,
    PUBLIC_ENTITY,
    PUBLICATION_DATE,
    PUBLICATION_TYPES,
    PRODUCTION_TYPES,
    SOURCE_TITLE,
    SOURCE_PUBLISHER,
    START_DATES,
    SUBMISSION_DATE,
    PROJECTS_TYPES,
    TYPES_FOR_COMPANIES,
    URBAN_HITS,
    YEARS,
    IS_OA;

    public static final String GLOBAL_FACET_ID = "global";
    public static final String FILTER_FACET_ID_PREFIX = "filter_";
    public static final String NAME_PREFIX = "facet_";

    private KeyTransformer keyTransformer;

    private FacetEnum(KeyTransformer keyTransformer) {
        this.keyTransformer = keyTransformer;
    }

    private FacetEnum() {
        //
    }

    public String getFacetName() {
        return NAME_PREFIX + this.toString().toLowerCase();
    }

    /**
     * Retourne la clé de la valeur de facette en fonction du type de la
     * facette.
     */
    public String bucketKeyAsString(Bucket bucket) {
        if (keyTransformer != null) {
            return keyTransformer.keyAsString(bucket.getKey());
        }

        return bucket.getKeyAsString();
    }

    public static FacetEnum valueFromFacetName(String name) {
        return valueOf(name.toUpperCase().substring(NAME_PREFIX.length()));
    }

    /**
     * Interface spécifiant une transformation de clé de valeur de facette.
     */
    private static interface KeyTransformer {

        /**
         * Retournbe la clé de la valeur de facette.
         *
         * @param key
         * @return
         */
        String keyAsString(Object key);

    }
}
