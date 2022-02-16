/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */

package fr.gouv.recherche.scanr.workflow.full;

import com.google.common.collect.Sets;
import fr.gouv.recherche.scanr.db.model.full.FullPublication;
import fr.gouv.recherche.scanr.db.model.full.FullPublicationField;

import java.util.Set;
import java.util.concurrent.locks.Lock;

public class FullPublicationTransaction implements AutoCloseable {

    private FullPublicationService service;
    private FullPublication publication;
    private Set<FullPublicationField> modifiedFields = Sets.newHashSet();
    private boolean created;

    public FullPublicationTransaction(FullPublicationService service, FullPublication publication, boolean created) {
        this.service = service;
        this.publication = publication;
        this.created = created;
    }

    public <E> void setField(FullPublicationField field, E data) {
        modifiedFields.addAll(service.setField(publication, field, data));
    }

    public <E> void refresh(FullPublicationField field) {
        modifiedFields.addAll(service.cleanDirtyFields(publication, Sets.newHashSet(field)));
    }

    public Set<FullPublicationField> getModifiedFields() {
        return modifiedFields;
    }

    public synchronized void save(boolean forcePropagation, boolean forIndex) {
        service.save(this, forcePropagation, forIndex);
    }

    public synchronized void delete(boolean forcePropagation) {
        service.delete(this, forcePropagation);
    }

    public FullPublication getData() {
        return publication;
    }

    @Override
    public synchronized void close(){}

    public boolean isCreated() {
        return created;
    }
}
