/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */

package fr.gouv.recherche.scanr.db.repository;

import fr.gouv.recherche.scanr.db.model.full.FullPublication;
import fr.gouv.recherche.scanr.db.model.full.FullPublicationField;

import java.util.stream.Stream;

public interface FullPublicationRepositoryCustom {

    Stream<FullPublication> streamAll(FullPublicationField... fields);

    Stream<String> streamIdsToIndex();

    Stream<String> selectAllIds();

    FullPublication findOne(String id, FullPublicationField... fields);

    boolean addDelayedFieldToRefresh(String id, FullPublicationField... fields);

    boolean notifyIndexed(String id);
}
