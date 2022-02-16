/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */

package fr.gouv.recherche.scanr.workflow.full;

import com.google.common.collect.Sets;
import fr.gouv.recherche.scanr.db.model.full.FullPerson;
import fr.gouv.recherche.scanr.db.model.full.FullPersonField;

import java.util.Set;
import java.util.concurrent.locks.Lock;

public class FullPersonTransaction implements AutoCloseable {

    private FullPersonService service;
    private FullPerson person;
    private Set<FullPersonField> modifiedFields = Sets.newHashSet();
    private boolean created;

    public FullPersonTransaction(FullPersonService service, FullPerson person, boolean created) {
        this.service = service;
        this.person = person;
        this.created = created;
    }

    public <E> void setField(FullPersonField field, E data) {
        modifiedFields.addAll(service.setField(person, field, data));
    }

    public <E> void refresh(FullPersonField field) {
        modifiedFields.addAll(service.cleanDirtyFields(person, Sets.newHashSet(field)));
    }

    public Set<FullPersonField> getModifiedFields() {
        return modifiedFields;
    }

    public synchronized void save(boolean forcePropagation, boolean forIndex) {
        service.save(this, forcePropagation, forIndex);
    }

    public synchronized void delete(boolean forcePropagation) {
        service.delete(this, forcePropagation);
    }

    public FullPerson getData() {
        return person;
    }

    @Override
    public synchronized void close() {}

    public boolean isCreated() {
        return created;
    }
}
