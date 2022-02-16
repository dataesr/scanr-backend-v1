/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.util;

import fr.gouv.recherche.scanr.db.model.I18nValue;
import org.apache.commons.lang3.NotImplementedException;

import java.util.HashMap;

public class DataTransformationUtils {

    /**
     * Convertit une HashMap en I18nValue (qui est une HashMap)
     * @param original
     * @return
     */
    public static I18nValue getI18nValueFromHashMap(HashMap original) {
        I18nValue destination = new I18nValue();
        destination.putAll(original);
        return destination;
    }
}
