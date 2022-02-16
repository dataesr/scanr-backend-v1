/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.search.model2.request;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.swagger.annotations.ApiModel;

/** used in (advanced) /.../search */
@ApiModel(value="v2.MultiValueSearchFilter", // else conflicts with v1
	parent=SearchFilter.class) // still doesn't show on UI
public class MultiValueSearchFilter extends SearchFilter {

    private Operator op;

    private List<String> values = new ArrayList<>();

    public enum Operator {
        all, any, none, not_all, exists
    }

    public MultiValueSearchFilter() {
    }

    public MultiValueSearchFilter(Operator op) {
        this.op = op;
    }

    public Operator getOp() {
        return op;
    }

    public List<String> getValues() {
        return values;
    }

    public MultiValueSearchFilter setValues(List<String> values) {
        this.values = values;
        return this;
    }

    public MultiValueSearchFilter addValue(String v) {
        if (v != null) {
            this.values.add(v);
        }
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MultiValueSearchFilter that = (MultiValueSearchFilter) o;
        return op == that.op &&
                Objects.equals(values, that.values);
    }

    @Override
    public int hashCode() {
        return Objects.hash(op, values);
    }
}
