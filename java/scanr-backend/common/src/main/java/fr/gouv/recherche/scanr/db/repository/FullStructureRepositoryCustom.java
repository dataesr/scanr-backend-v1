/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.db.repository;

import fr.gouv.recherche.scanr.db.model.full.FullStructure;
import fr.gouv.recherche.scanr.db.model.full.FullStructureField;

import java.util.stream.Stream;


public interface FullStructureRepositoryCustom {
    Stream<String> selectAllIds();
    public FullStructure findOne(String id, FullStructureField... fields);

    public Stream<FullStructure> streamAll(FullStructureField... fields);

    public Stream<String> streamIdsToIndex();

    boolean addDelayedFieldToRefresh(String id, FullStructureField... fields);

    boolean notifyIndexed(String id);
}
