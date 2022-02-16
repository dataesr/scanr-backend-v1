/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.db.repository;

import java.util.stream.Stream;


public interface StructureRepositoryCustom {
    Stream<String> streamAllIds();
    Stream<String> streamAllIdsByLinkId(String websiteId);

}
