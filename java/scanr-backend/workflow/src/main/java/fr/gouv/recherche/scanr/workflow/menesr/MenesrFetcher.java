/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.workflow.menesr;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.gouv.recherche.scanr.common.http.DPHttpClient;
import fr.gouv.recherche.scanr.common.http.DPHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;


@Service
public class MenesrFetcher {
    private static final Logger log = LoggerFactory.getLogger(MenesrFetcher.class);

    @Autowired
    private DPHttpClient client;

    @Autowired
    public MenesrConfiguration config;

    private final ObjectMapper om;

    public MenesrFetcher() {
        om = new ObjectMapper();
        om.findAndRegisterModules();
        om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        om.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        om.enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL);
        om.enable(JsonParser.Feature.ALLOW_COMMENTS);
    }

    public <E> List<E> fetchResources(Class<E> clazz) {
        List<E> resourcesList;

        try {
            String $filepath = config.getResourceFilePath(clazz);
            resourcesList = fetchResources(clazz, $filepath);
        } catch (IllegalAccessException | InstantiationException e) {
            // Should not happen if the application is correctly configured, and json well formed, crash the task
            throw new IllegalStateException(e);
        }

        return resourcesList;
    }

    public <E> List<E> fetchResources(Class<E> clazz, String $filepath) {
        log.info("Fetching " + clazz.getSimpleName() + " from MENESR");

        File $file = new File($filepath);
        List<E> resources = null;
        try {
            resources = deserializeJsonListFile($file, clazz);
        } catch (IOException e) {
            // Should not happen if the application is correctly configured, and json well formed, crash the task
            throw new IllegalStateException(e);
        }
        return resources;
    }

    protected <E> List<E> deserializeJsonListFile(File $jsonFile, Class<E> clazz) throws IOException {
        log.info("JSON deserialization for file '" + $jsonFile.getName() + "'");

        return om.readValue($jsonFile, om.getTypeFactory().constructCollectionType(List.class, clazz));
    }

    protected <E> List<E> fetch(HttpGet action, Class<E> clazz) {
        try {
            DPHttpResponse response = client.execute(action);
            List<E> extracted = om.readValue(response.text(), om.getTypeFactory().constructCollectionType(List.class, clazz));
            log.debug("... fetch success");
            return extracted;
        } catch (IOException e) {
            // Should not happen if the application is correctly configured, crash the task
            throw new IllegalStateException(e);
        }
    }
}
