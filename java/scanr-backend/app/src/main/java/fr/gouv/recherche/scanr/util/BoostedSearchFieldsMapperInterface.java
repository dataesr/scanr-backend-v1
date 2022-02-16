/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */

package fr.gouv.recherche.scanr.util;

import java.util.HashMap;

public interface BoostedSearchFieldsMapperInterface {

    String DEFAULT_KEY = "default";
    Float DEFAULT_BOOST = 1f;

    /**
     * Retourne la map des boosts définis pour les différents champs
     *
     * @return
     */
    HashMap<String, Float> getBoostConfiguration();

    /**
     * Retourne le boost défini pour le champ donné
     *
     * @param fieldName
     * @return
     */
    Float getBoostForField(String fieldName);
}
