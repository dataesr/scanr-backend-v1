/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.config.elasticsearch;

public enum EsFilePath {
    MAPPING_STRUCTURE("/mapping_es/mapping_structure.json"),
    MAPPING_PUBLICATION("/mapping_es/mapping_structure.json"),
    MAPPING_PROJECT("/mapping_es/mapping_structure.json"),
    MAPPING_PERSONS("/mapping_es/mapping_structure.json"),
    INDEX_SETTINGS_DEFAULT("/mapping_es/index_settings_default.json");

    protected String fileName;

    private EsFilePath(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }
}
