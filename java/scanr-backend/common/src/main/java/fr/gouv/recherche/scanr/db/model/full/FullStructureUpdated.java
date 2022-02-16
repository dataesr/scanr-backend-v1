/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.db.model.full;

import java.util.Date;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonInclude;
import fr.gouv.recherche.scanr.db.model.full.FullStructureField;

/**
 * Stores the modification to be done on a full structure (to batch the modifiations of a struture).
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class FullStructureUpdated {
    private String id;
    private Date lastUpdated;
    private Set<FullStructureField> modifiedFields;

    public FullStructureUpdated() {
    }

    public FullStructureUpdated(String id, Date lastUpdated, Set<FullStructureField> modifiedFields) {
        this.id = id;
        this.lastUpdated = lastUpdated;
        this.modifiedFields = modifiedFields;
    }

    public String getId() {
        return id;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public Set<FullStructureField> getModifiedFields() {
        return modifiedFields;
    }
}
