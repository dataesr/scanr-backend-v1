/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.db.repository;

import fr.gouv.recherche.scanr.db.model.Project;

import java.util.List;
import java.util.stream.Stream;

public interface ProjectRepositoryCustom {
    Stream<Project> streamEntities();
    Stream<String> streamAllIds();

    /**
     * Return 'limit' projects matching the given regex
     *
     * @param regex
     * @return
     */
    List<Project> findByAcronymLike(String regex, int limit);
}
