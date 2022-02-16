/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */

package fr.gouv.recherche.scanr.db.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Statistique {
    private long nbCreated;
    private long nbDeleted;
    private long diffCreationDeleted;

    public long getNbCreated() {
        return nbCreated;
    }

    public void setNbCreated(long nbCreated) {
        this.nbCreated = nbCreated;
    }

    public long getNbDeleted() {
        return nbDeleted;
    }

    public void setNbDeleted(long nbDeleted) {
        this.nbDeleted = nbDeleted;
    }

    public long getDiffCreationDeleted() {
        return diffCreationDeleted;
    }

    public void setDiffCreationDeleted(long diffCreationDeleted) {
        this.diffCreationDeleted = diffCreationDeleted;
    }
}
