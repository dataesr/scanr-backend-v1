/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.elasticsearch.common.geo.GeoPoint;

import java.io.IOException;

/**
 * Deserialize a structure such as {
 * "lat": 43.3025,
 * "lon": 5.4017
 * } in a Point[longitude, latitude] (longitude first)
 */

public class GeoDeserializer extends JsonDeserializer<GeoPoint> {

    public static final String LAT = "lat";
    public static final String LON = "lon";

    @Override
    public GeoPoint deserialize(JsonParser jp, DeserializationContext deserializationContext) throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);
        if (node != null) {
            JsonNode latNode = node.get(LAT);
            JsonNode lonNode = node.get(LON);
            if (latNode != null && latNode.isNumber() && lonNode != null && lonNode.isNumber()) {
                double lat = latNode.doubleValue();
                double lon = lonNode.doubleValue();
                return new GeoPoint(lat, lon);
            }
        }
        return null;
    }

}
