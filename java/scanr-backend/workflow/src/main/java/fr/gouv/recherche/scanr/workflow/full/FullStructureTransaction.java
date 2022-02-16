/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.workflow.full;

import fr.gouv.recherche.scanr.db.model.full.FullStructure;
import fr.gouv.recherche.scanr.db.model.full.FullStructureField;
import com.google.common.collect.Sets;

import java.util.Set;
import java.util.concurrent.locks.Lock;

/**
 *
 */
public class FullStructureTransaction implements AutoCloseable {
    private FullStructureService service;
    private FullStructure structure;
    private Set<FullStructureField> modifiedFields = Sets.newHashSet();
    private boolean created;

    public FullStructureTransaction(FullStructureService service, FullStructure structure, boolean created) {
        this.service = service;
        this.structure = structure;
        this.created = created;
    }

    public <E> void setField(FullStructureField field, E data) {
        modifiedFields.addAll(service.setField(structure, field, data));
    }

    public <E> void refresh(FullStructureField field) {
        modifiedFields.addAll(service.cleanDirtyFields(structure, Sets.newHashSet(field)));
    }

    public Set<FullStructureField> getModifiedFields() {
        return modifiedFields;
    }

    public synchronized void save(boolean forcePropagation, boolean forIndex) {
        service.save(this, forcePropagation, forIndex);
    }

    public synchronized void delete(boolean forcePropagation) {
        service.delete(this, forcePropagation);
    }

    public FullStructure getData() {
        return structure;
    }

    @Override
    public synchronized void close() {}

    public boolean isCreated() {
        return created;
    }
}
