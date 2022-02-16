/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.api.exception;

import org.springframework.http.HttpStatus;

/**
 *
 */
public interface MvcException<E> {
    public HttpStatus getStatus();

    public E getBody();
}
