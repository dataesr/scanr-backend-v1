/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */

package fr.gouv.recherche.scanr.db.model;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class InstitutionRelationTest {

    private ObjectMapper om;

    @Before
    public void setUp() {

        // OM used in MenesrFetcher
        om = new ObjectMapper();
        om.findAndRegisterModules();
        om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        om.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        om.enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL);
        om.enable(JsonParser.Feature.ALLOW_COMMENTS);
    }

    @Test
    public void testJsonDeserializeId() {
        String json = "\t[{\n" +
                "\t\t\"structure\": \"154785\",\n" +
                "\t\t\"label\": \"Universite Haute Alsace Mulhouse\",\n" +
                "\t\t\"url\": null,\n" +
                "\t\t\"code\": null,\n" +
                "\t\t\"relationType\": \"etablissement participant\",\n" +
                "\t\t\"fromDate\": \"2015-01-01T00:00:00.000+0000\"\n" +
                "\t}]";

        try {
            List<InstitutionRelation> read = om.readValue(json, om.getTypeFactory().constructCollectionType(List.class, InstitutionRelation.class));

            InstitutionRelation institutionRelation = new InstitutionRelation();
            Structure structure = new Structure();
            structure.setId("154785");
            institutionRelation.setStructure(structure);

            assertEquals(1, read.size());
            assertEquals(institutionRelation.getStructure().getId(), read.get(0).getStructure().getId());

        } catch (IOException e) {
            e.printStackTrace();
            assertFalse(false);
        }
    }

    @Test
    public void testJsonDeserializeObject() {
        String json = "[{\n" +
                "\t\t\"structure\": {\n" +
                "\t\t\t\"id\": \"154785\",\n" +
                "\t\t\t\"kind\": null,\n" +
                "\t\t\t\"logo\": null,\n" +
                "\t\t\t\"label\": null,\n" +
                "\t\t\t\"acronym\": null,\n" +
                "\t\t\t\"nature\": null,\n" +
                "\t\t\t\"status\": null,\n" +
                "\t\t\t\"isFrench\": false,\n" +
                "\t\t\t\"address\": [],\n" +
                "\t\t\t\"commercialBrands\": null,\n" +
                "\t\t\t\"alias\": null,\n" +
                "\t\t\t\"description\": null,\n" +
                "\t\t\t\"isPublic\": null,\n" +
                "\t\t\t\"parents\": null,\n" +
                "\t\t\t\"predecessors\": null,\n" +
                "\t\t\t\"legalCategory\": null,\n" +
                "\t\t\t\"level\": null,\n" +
                "\t\t\t\"creationYear\": null,\n" +
                "\t\t\t\"links\": null,\n" +
                "\t\t\t\"institutions\": null,\n" +
                "\t\t\t\"leaders\": null,\n" +
                "\t\t\t\"relations\": null,\n" +
                "\t\t\t\"activities\": null,\n" +
                "\t\t\t\"domains\": null,\n" +
                "\t\t\t\"employeesInfo\": null,\n" +
                "\t\t\t\"finance\": null,\n" +
                "\t\t\t\"badges\": null,\n" +
                "\t\t\t\"spinoffs\": null,\n" +
                "\t\t\t\"startDate\": null,\n" +
                "\t\t\t\"endDate\": null,\n" +
                "\t\t\t\"email\": null,\n" +
                "\t\t\t\"phone\": null,\n" +
                "\t\t\t\"socialMedias\": null,\n" +
                "\t\t\t\"externalIds\": {},\n" +
                "\t\t\t\"evaluations\": null,\n" +
                "\t\t\t\"offers\": null,\n" +
                "\t\t\t\"focus\": null,\n" +
                "\t\t\t\"keywords\": null,\n" +
                "\t\t\t\"createdDate\": null,\n" +
                "\t\t\t\"lastUpdated\": null,\n" +
                "\t\t\t\"createdAt\": null,\n" +
                "\t\t\t\"removedAt\": null\n" +
                "\t\t},\n" +
                "\t\t\"label\": \"Universite Haute Alsace Mulhouse\",\n" +
                "\t\t\"url\": null,\n" +
                "\t\t\"code\": null,\n" +
                "\t\t\"relationType\": \"etablissement participant\",\n" +
                "\t\t\"fromDate\": \"2015-01-01T00:00:00.000+0000\"\n" +
                "\t}]";

        try {
            List<InstitutionRelation> read = om.readValue(json, om.getTypeFactory().constructCollectionType(List.class, InstitutionRelation.class));

            InstitutionRelation institutionRelation = new InstitutionRelation();
            Structure structure = new Structure();
            structure.setId("154785");
            institutionRelation.setStructure(structure);

            assertEquals(1, read.size());
            assertEquals(institutionRelation.getStructure().getId(), read.get(0).getStructure().getId());

        } catch (IOException e) {
            e.printStackTrace();
            assertFalse(false);
        }
    }
}