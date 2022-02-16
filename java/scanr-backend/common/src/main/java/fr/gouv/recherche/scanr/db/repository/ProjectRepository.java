/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.db.repository;

import fr.gouv.recherche.scanr.db.model.Project;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Collection;
import java.util.List;

public interface ProjectRepository extends MongoRepository<Project, String>, ProjectRepositoryCustom {
    @Query(value = "{_id: {'$in': ?0}}", fields = "{structures:1}")
    List<Project> findProjectsByStructureIds(Collection<String> projectIds);

    @Query(value = "{'persons.person._id': ?0}")
    List<Project> findProjectByPersonId(String personId);

    @Query(value = "{'participants.structure._id': ?0}")
    List<Project> findProjectByStructureId(String structureId);

    @Query(value = "{}", fields = "{acronym:1, label:1}")
    List<Project> findAllNames();

    @Query(value = "{_id: {'$in': ?0}}", fields = "{_id: 1, type:1, label:1, acronym:1, year:1, startDate:1, endDate:1, budgetTotal:1, budgetFinanced:1, duration:1, url:1, projectUrl:1, participantCount:1}")
    List<Project> findByIdsLight(Collection<String> id);

    @Query(value = "{_id: {'$in': ?0}}", delete = true)
    void deleteByIds(Collection<String> id);
}
