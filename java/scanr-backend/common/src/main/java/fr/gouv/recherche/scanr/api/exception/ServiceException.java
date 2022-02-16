/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.api.exception;

public class ServiceException extends Exception {

    private static final long serialVersionUID = -2416166237260591234L;

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(String message, Exception ex) {
        super(message, ex);
    }
}
