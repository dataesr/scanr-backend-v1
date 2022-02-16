/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.search.model2.request;


import org.elasticsearch.common.geo.GeoPoint;

import io.swagger.annotations.ApiModel;

import java.util.Objects;

/** used in used in (advanced) /.../search, was in v1 but not used */
@ApiModel(value="v2.GeoGridFilter", // else conflicts with v1
	description="Allows an Elasticsearch geo_bounding_box query, see "
		+ "https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-geo-bounding-box-query.html")
public class GeoGridFilter extends SearchFilter {

    public GeoPoint topLeft;
    public GeoPoint bottomRight;

    public GeoGridFilter() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GeoGridFilter that = (GeoGridFilter) o;
        return Objects.equals(topLeft, that.topLeft) &&
                Objects.equals(bottomRight, that.bottomRight);
    }

    @Override
    public int hashCode() {
        return Objects.hash(topLeft, bottomRight);
    }
}
