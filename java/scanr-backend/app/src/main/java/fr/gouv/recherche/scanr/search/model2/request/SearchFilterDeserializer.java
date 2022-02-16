/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */

package fr.gouv.recherche.scanr.search.model2.request;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;


/**
 * Allows implicit polymorphic deserializations for impls of SearchFilter
 * @author mdutoo
 *
 */
public class SearchFilterDeserializer extends JsonDeserializer<SearchFilter> {
    
    @Override
    public SearchFilter deserialize(JsonParser p, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        if (p.getCurrentToken() == JsonToken.START_OBJECT) {
        	JsonNode searchFilterNode = p.readValueAsTree();
        	
        	if (searchFilterNode.get("values") != null) {
        		return p.getCodec().treeToValue(searchFilterNode, MultiValueSearchFilter.class);
        		
        	} else if (searchFilterNode.get("topLeft") != null || searchFilterNode.get("topRight") != null) {
        		return p.getCodec().treeToValue(searchFilterNode, GeoGridFilter.class);
        		
        	} else if (searchFilterNode.get("min") != null && searchFilterNode.get("min").canConvertToLong()
        			|| searchFilterNode.get("max") != null && searchFilterNode.get("max").canConvertToLong()) {
        		return p.getCodec().treeToValue(searchFilterNode, LongRangeFilter.class);
        		
        	} else if (searchFilterNode.get("min") != null && searchFilterNode.get("min").isTextual()
        			|| searchFilterNode.get("max") != null && searchFilterNode.get("max").isTextual()) {
        		return p.getCodec().treeToValue(searchFilterNode, DateRangeFilter.class);
        	}
        	
        	/*
        	// alternate solution :
        	try {
        		return p.getCodec().treeToValue(searchFilterNode, LongRangeFilter.class);
        	} catch (Exception ex) {
        		log.error("Not a long filter", ex);
        	}
        	*/
        	
            throw new IllegalStateException("Can't detect SearchFilter concrete class for "
                    + searchFilterNode);
        }
        throw new IllegalStateException("Attempting to deserialize SearchFilter "
                + "and expected START_OBJECT token but found " + p.getCurrentToken());
    }
}
