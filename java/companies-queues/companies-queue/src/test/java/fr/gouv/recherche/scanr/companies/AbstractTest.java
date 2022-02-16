/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.companies;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.beans.factory.config.PropertyResourceConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 *
 */
@ActiveProfiles({"test", "workflow"})
@WebAppConfiguration
@Configuration
@ComponentScan(basePackages = "fr.gouv.recherche.scanr")
@ContextConfiguration(classes = {AbstractTest.class})
@RunWith(SpringJUnit4ClassRunner.class)
public abstract class AbstractTest {

    /**
     * @return
     */
    @Bean
    public PropertyResourceConfigurer propertyResourceConfigurer() {
        final PropertyPlaceholderConfigurer configurer = new PropertyPlaceholderConfigurer();
        configurer.setLocation(new ClassPathResource("/scanr_companies_queue.properties"));
        configurer.setNullValue("@null");
        return configurer;
    }

}
