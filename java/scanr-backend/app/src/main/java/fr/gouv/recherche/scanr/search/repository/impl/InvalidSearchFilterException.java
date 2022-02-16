/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.search.repository.impl;

import fr.gouv.recherche.scanr.search.model2.request.MultiValueSearchFilter;

/**
 * TODO: catch this excepttion and transform to MvcException
 * Ne garder qu'une des 2 exceptions doublon de celle dans fr.gouv.recherche.scanr.search.exceptions
 */
public class InvalidSearchFilterException extends RuntimeException {

    public static class Body {
        public MultiValueSearchFilter filter;
        public String reason;
        public String attribute;

        public Body(MultiValueSearchFilter filter, String reason) {
            this.filter = filter;
            this.reason = reason;
        }

        public Body(MultiValueSearchFilter filter, String attribute, String reason) {
            this.filter = filter;
            this.reason = reason;
            this.attribute = attribute;
        }
    }

    private final Body body;

    public InvalidSearchFilterException(MultiValueSearchFilter filter, String reason) {
        this.body = new Body(filter, reason);
    }

    public InvalidSearchFilterException(MultiValueSearchFilter filter, String attribute, String reason) {
        this.body = new Body(filter, attribute, reason);
    }

    public Body getBody() {
        return body;
    }
}
