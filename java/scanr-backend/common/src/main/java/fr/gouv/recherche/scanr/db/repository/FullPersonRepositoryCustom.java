/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */

package fr.gouv.recherche.scanr.db.repository;

import fr.gouv.recherche.scanr.db.model.full.FullPerson;
import fr.gouv.recherche.scanr.db.model.full.FullPersonField;

import java.util.stream.Stream;

public interface FullPersonRepositoryCustom {

    Stream<FullPerson> streamAll(FullPersonField... fields);

    Stream<String> streamIdsToIndex();

    Stream<String> selectAllIds();

    FullPerson findOne(String id, FullPersonField... fields);

    boolean addDelayedFieldToRefresh(String id, FullPersonField... fields);

    boolean notifyIndexed(String id);
}
