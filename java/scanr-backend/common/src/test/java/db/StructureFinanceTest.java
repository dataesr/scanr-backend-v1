/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package db;

import fr.gouv.recherche.scanr.db.model.StructureFinance;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class StructureFinanceTest {
    @Test
    public void testGeoDeserialization() throws IOException {
        String json = "{\n" +
                "    \"employeesField\": \"Plein d'employés\",\n" +
                "    \"employeesCategory\": 83,\n" +
                "    \"ecRatio\": 0.3735,\n" +
                "    \"researchersPayroll\": [\n" +
                "        {\n" +
                "            \"id\": \"180089013\",\n" +
                "            \"label\": \"Centre national de la recherche scientifique (CNRS)\",\n" +
                "            \"url\": \"scanr/structure/180089013\",\n" +
                "            \"texte_survol\": null\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"196917751\",\n" +
                "            \"label\": \"Université Lumière - Lyon 2\",\n" +
                "            \"url\": \"scanr/structure/196917751\",\n" +
                "            \"texte_survol\": null\n" +
                "        }\n" +
                "    ],\n" +
                "    \"hdr\": 14,\n" +
                "    \"domainRatios\": [\n" +
                "        {\n" +
                "            \"id\": \"SHS6_1 Histoire\",\n" +
                "            \"label\": \"Histoire\"\n" +
                "        }\n" +
                "    ]\n" +
                "}";
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        StructureFinance structureFinance = objectMapper.readValue(json, StructureFinance.class);
//        assertEquals("Plein d'employés", structureFinance.getEmployeesField());
//        assertEquals("25 - 50 % de chercheurs et enseignants-chercheurs", structureFinance.getEcField());
//        assertEquals("HDR : + de 10 personnes", structureFinance.getHdrField());
    }
}
