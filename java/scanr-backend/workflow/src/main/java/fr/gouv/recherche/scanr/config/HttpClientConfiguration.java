/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.config;

import fr.gouv.recherche.scanr.common.http.DPHttpClient;
import fr.gouv.recherche.scanr.common.http.impl.DPHttpClientImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class HttpClientConfiguration {
    @Bean
    public DPHttpClient dpHttpClient() {
        return new DPHttpClientImpl();
    }
}
