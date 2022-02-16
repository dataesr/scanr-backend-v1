/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.api.exception.handler;

import fr.gouv.recherche.scanr.api.exception.MvcException;
import fr.gouv.recherche.scanr.api.exception.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.net.MalformedURLException;

/**
 *
 */
@ControllerAdvice
public class MvcExceptionHandler extends JsonEntityExceptionHandler {

    @ExceptionHandler({NotFoundException.class})
    protected ResponseEntity<Object> handleMvcExceptionInterface(MvcException<?> e, WebRequest request) {
        return handleMvcException(e, request);
    }

    @ExceptionHandler({MalformedURLException.class, IllegalArgumentException.class})
    protected ResponseEntity<Object> handleMalformedURLException(Exception e, WebRequest request) {
        return handleExceptionInternal(e, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler({Exception.class})
    protected ResponseEntity<Object> handleUnknownException(Exception e, WebRequest request) {
        return handleExceptionInternal(e, HttpStatus.INTERNAL_SERVER_ERROR, request);
    }
}
