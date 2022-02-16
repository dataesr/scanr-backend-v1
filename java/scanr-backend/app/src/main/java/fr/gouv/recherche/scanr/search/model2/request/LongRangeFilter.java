/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.search.model2.request;

import io.swagger.annotations.ApiModel;

/** used in (advanced) /.../search ex. Projet.subventions (or double ??), not in v1 */
@ApiModel("v2.LongRangeFilter") // else conflicts with v1
public class LongRangeFilter extends RangeFilter<Long> {

    public LongRangeFilter() {
    }

    public LongRangeFilter(Long min, Long max, Boolean missing) {
        super(min, max, missing);
    }

}
