/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.elasticsearch.common.geo.GeoPoint;

import java.io.IOException;

public class GeoSerializer extends JsonSerializer<GeoPoint> {

    @Override
    public void serialize(GeoPoint value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
        gen.writeStartObject();
        gen.writeNumberField("lat", value.getLat());
        gen.writeNumberField("lon", value.getLon());
        gen.writeEndObject();
    }
}
