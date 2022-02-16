/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.File;


@Configuration
public class ScreenshotConfiguration {

    @Value("${screenshot.storage:/tmp/screenshots}")
    private String storagePath;

    @PostConstruct
    private void init() {
        File dir = new File(storagePath);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                throw new RuntimeException("Cannot storage directory "+storagePath);
            }
        }
    }

    public String getStoragePath() {
        return storagePath;
    }
}
