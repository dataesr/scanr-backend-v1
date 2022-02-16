/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.db.model;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Abstract class to store ranges
 *
 * @param <T>
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public abstract class Range<T> {
    protected T min;
    protected T max;

    public Range(T min, T max) {
        this.min = min;
        this.max = max;
    }

    public T getMin() {
        return min;
    }

    public T getMax() {
        return max;
    }
}
