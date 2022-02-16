/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */

package fr.gouv.recherche.scanr.workflow.full;

import com.google.common.collect.Sets;
import fr.gouv.recherche.scanr.db.model.full.FullProject;
import fr.gouv.recherche.scanr.db.model.full.FullProjectField;

import java.util.Set;
import java.util.concurrent.locks.Lock;

public class FullProjectTransaction implements AutoCloseable {

    private FullProjectService service;
    private FullProject project;
    private Set<FullProjectField> modifiedFields = Sets.newHashSet();
    private boolean created;

    public FullProjectTransaction(FullProjectService service, FullProject project, boolean created) {
        this.service = service;
        this.project = project;
        this.created = created;
    }

    public <E> void setField(FullProjectField field, E data) {
        modifiedFields.addAll(service.setField(project, field, data));
    }

    public <E> void refresh(FullProjectField field) {
        modifiedFields.addAll(service.cleanDirtyFields(project, Sets.newHashSet(field)));
    }

    public Set<FullProjectField> getModifiedFields() {
        return modifiedFields;
    }

    public synchronized void save(boolean forcePropagation, boolean forIndex) {
        service.save(this, forcePropagation, forIndex);
    }

    public synchronized void delete(boolean forcePropagation) {
        service.delete(this, forcePropagation);
    }

    public FullProject getData() {
        return project;
    }

    @Override
    public synchronized void close() {}

    public boolean isCreated() {
        return created;
    }
}
