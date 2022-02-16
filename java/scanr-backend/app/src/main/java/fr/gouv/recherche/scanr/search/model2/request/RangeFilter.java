/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.search.model2.request;

import java.util.Objects;

import io.swagger.annotations.ApiModel;

/**
 * used in (advanced) /.../search in extending filters, not in v1
 * @author Jacques Belissent
 */
@ApiModel("v2.DateRangeFilter") // else conflicts with v1
public class RangeFilter<T extends Comparable<T>> extends SearchFilter {

    public T min;
    public T max;
    public Boolean missing = Boolean.FALSE;

    public RangeFilter() {}

    public RangeFilter(T min, T max, Boolean missing) {
        this.min = min;
        this.max = max;
        this.missing = missing;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RangeFilter<?> that = (RangeFilter<?>) o;
        return Objects.equals(min, that.min) &&
                Objects.equals(max, that.max) &&
                Objects.equals(missing, that.missing);
    }

    @Override
    public int hashCode() {
        return Objects.hash(min, max, missing);
    }
}
