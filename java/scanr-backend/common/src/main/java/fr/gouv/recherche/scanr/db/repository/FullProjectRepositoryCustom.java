/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */

package fr.gouv.recherche.scanr.db.repository;

import fr.gouv.recherche.scanr.db.model.full.FullProject;
import fr.gouv.recherche.scanr.db.model.full.FullProjectField;

import java.util.stream.Stream;

public interface FullProjectRepositoryCustom {

    Stream<FullProject> streamAll(FullProjectField... fields);

    Stream<String> streamIdsToIndex();

    Stream<String> selectAllIds();

    FullProject findOne(String id, FullProjectField... fields);

    boolean addDelayedFieldToRefresh(String id, FullProjectField... fields);

    boolean notifyIndexed(String id);
}
