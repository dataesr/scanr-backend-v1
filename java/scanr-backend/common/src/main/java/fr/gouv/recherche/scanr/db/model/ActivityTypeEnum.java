/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.db.model;

/**
 * activity types
 */

public enum ActivityTypeEnum {
    NAF("NAF"), DOMAINE("Domaine scientifique"), ERC("Discipline ERC"), THEME("Thème");

    private String label;

    ActivityTypeEnum(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
