/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.search.exceptions;

import fr.gouv.recherche.scanr.search.model2.request.SearchFilter;

/**
 * TODO: catch this excepttion and transform to MvcException
 */
public class InvalidSearchFilterException extends RuntimeException {

    public static class Body {
        public SearchFilter filter;
        public String reason;
        public String attribute;

        public Body(SearchFilter filter, String reason) {
            this.filter = filter;
            this.reason = reason;
        }

        public Body(SearchFilter filter, String attribute, String reason) {
            this.filter = filter;
            this.reason = reason;
            this.attribute = attribute;
        }
    }

    private final Body body;

    public InvalidSearchFilterException(SearchFilter filter, String reason) {
        this.body = new Body(filter, reason);
    }

    public InvalidSearchFilterException(SearchFilter filter, String attribute, String reason) {
        this.body = new Body(filter, attribute, reason);
    }

    public Body getBody() {
        return body;
    }
}
