/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.db.repository;

import fr.gouv.recherche.scanr.db.model.Structure;
import org.springframework.data.domain.Pageable;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Collection;
import java.util.List;

public interface StructureRepository extends MongoRepository<Structure, String>, StructureRepositoryCustom {

    @Query(value = "{_id: {'$in': ?0}}", fields = "{_id: 1, kind: 1, label: 1, logo: 1, acronym: 1, nature: 1, status: 1, isFrench: 1, address: 1}")
    List<Structure> findByIdsLight(Collection<String> id);

    @Query(value = "{_id: ?0}", fields = "{_id: 1, kind: 1, label: 1, logo: 1, acronym: 1, nature: 1, status: 1, isFrench: 1, address: 1}")
    Structure findByIdLight(String id);

    @Query(value = "{'parents.structure._id': ?0}", fields = "{_id: 1, kind: 1, label: 1, logo: 1, acronym: 1, nature: 1, status: 1, isFrench: 1, address: 1}")
    List<Structure> findByParentIdLight(String id);

    @Query(value = "{'relations._id': {'$in': ?0}}", fields = "{_id: 1}")
    List<Structure> findIdsByRelationId(Collection<String> relationIds);

    @Query(value = "{'institutions.code.normalized': {'$in': ?0}}", fields = "{_id: 1}")
    List<Structure> findIdsByInstitutionCode(Collection<String> normalizedCodes);

    @Query(value = "{'spinoffs.project._id': ?0}")
    List<Structure> findStructureByProjectIdInSpinoff(String projectId);

    @Query(value = "{'spinoffs.structure._id': ?0}")
    List<Structure> findStructureByStructureIdInSpinoff(String structureId);

    @Query(value = "{'leaders.person._id': ?0}")
    List<Structure> findStructureByPersonId(String personId);

    @Query(value = "{_id: {'$in': ?0}}", delete = true)
    void deleteByIds(Collection<String> id);

    List<Structure> findByAddressGpsNear(Point point, Distance distance, Pageable request);
}
