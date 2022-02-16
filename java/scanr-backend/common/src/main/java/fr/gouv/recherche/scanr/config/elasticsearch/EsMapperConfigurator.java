/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.config.elasticsearch;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.sword.utils.elasticsearch.intf.IMapperConfigurator;

public class EsMapperConfigurator implements IMapperConfigurator {

    private static final EsMapperConfigurator instance = new EsMapperConfigurator();

    public static EsMapperConfigurator getInstance() {
        return instance;
    }

    private EsMapperConfigurator() {
        super();
    }

    @Override
    public void configureMapper(ObjectMapper mapper) {
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }
}
