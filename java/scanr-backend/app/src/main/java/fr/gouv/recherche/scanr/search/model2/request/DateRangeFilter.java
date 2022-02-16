/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.search.model2.request;

import org.springframework.format.annotation.DateTimeFormat;

import io.swagger.annotations.ApiModel;

import java.util.Date;

/** used in (advanced) /.../search ex. in Project.startDate, not in v1 */
@ApiModel("v2.DateRangeFilter") // else conflicts with v1
public class DateRangeFilter extends RangeFilter<Date> {
    public DateRangeFilter() {}

    public DateRangeFilter(Date min, Date max, Boolean missing) {
        super(min, max, missing);
    }


    @DateTimeFormat(pattern = "yyyy-MM-dd")
    public Date getMin() {
        return min;
    }

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    public void setMin(Date min) {
        this.min = min;
    }

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    public Date getMax() {
        return max;
    }

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    public void setMax(Date max) {
        this.max = max;
    }
}
