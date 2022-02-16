/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.api.util;

/**
 *
 */
public class ApiConstants {
    public static final class OK {
        public boolean ok = true;
    }

    public static final String PRODUCES_JSON = "application/json; charset=UTF-8";
    public static final OK OK_MESSAGE = new OK();
    public static final String DEFAULT_LANGUAGE = "default";
}
