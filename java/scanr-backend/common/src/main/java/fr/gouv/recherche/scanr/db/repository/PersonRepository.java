/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */

package fr.gouv.recherche.scanr.db.repository;

import fr.gouv.recherche.scanr.db.model.Person;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Collection;
import java.util.List;

public interface PersonRepository extends MongoRepository<Person, String>, PersonRepositoryCustom {

    @Query(value = "{_id: {'$in': ?0}}", fields = "{_id: 1, firstName: 1, lastName: 1, maidenName: 1, fullName: 1, gender: 1, roles: 1, affiliations: 1, externalIds: 1, awards: 1}")
    List<Person> findByIdsLight(Collection<String> id);

    @Query(value = "{'affiliations.structure._id': ?0}")
    List<Person> findPersonByStructureIdInAffiliation(String structureId);

    @Query(value = "{ $or: [ {'affiliations.structure._id': ?0}, {'awards.structure._id': ?0} ] }")
    List<Person> findPersonByStructureId(String structureId);

    @Query(value = "{_id: {'$in': ?0}}", delete = true)
    void deleteByIds(Collection<String> id);
}
