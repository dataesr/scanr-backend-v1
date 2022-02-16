/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.api.exception;

public class ElasticException extends Exception {

    private static final long serialVersionUID = 3433547135272033834L;

    public ElasticException(String message) {
        super(message);
    }

    public ElasticException(String message, Throwable cause) {
        super(message, cause);
    }
}
