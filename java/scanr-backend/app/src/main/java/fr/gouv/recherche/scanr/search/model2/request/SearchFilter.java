/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */

package fr.gouv.recherche.scanr.search.model2.request;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import io.swagger.annotations.ApiModel;

/**
 * (or abstract class)
 * 
 * BEWARE Jackson polymorphism configuration is NOT USED besides by Swagger, because uses a custom deserializer
 * BUT neither Swagger conf nor Jackson polymorphism are able to show inheriting impls in API doc UI,
 * so rather dummy fields
 * * 
 * @author mdutoo
 *
 */
@ApiModel(value="v2.SearchFilter",
	subTypes= {MultiValueSearchFilter.class, GeoGridFilter.class, LongRangeFilter.class, DateRangeFilter.class}) // still doesn't show on UI
@JsonTypeInfo( use = JsonTypeInfo.Id.NAME, // Were binding by providing a name
include = JsonTypeInfo.As.PROPERTY, // The name is provided in a property
property = "type" // Property name is type
//visible = true // Retain the value of type after deserialisation
)
@JsonSubTypes({//Below, we define the names and the binding classes.
	@JsonSubTypes.Type(value = MultiValueSearchFilter.class, name = "MultiValueSearchFilter"),
	@JsonSubTypes.Type(value = GeoGridFilter.class, name = "GeoGridFilter"),
	@JsonSubTypes.Type(value = LongRangeFilter.class, name = "LongRangeFilter"),
	@JsonSubTypes.Type(value = DateRangeFilter.class, name = "DateRangeFilter")
})
public abstract class SearchFilter {
	
}
