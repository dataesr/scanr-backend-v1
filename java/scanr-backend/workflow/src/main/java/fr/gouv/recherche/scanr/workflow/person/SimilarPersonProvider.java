/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */

package fr.gouv.recherche.scanr.workflow.person;

import com.google.common.collect.Sets;
import fr.gouv.recherche.scanr.db.model.Person;
import fr.gouv.recherche.scanr.db.model.SimilarTypedObject;
import fr.gouv.recherche.scanr.db.model.full.FullPerson;
import fr.gouv.recherche.scanr.db.model.full.FullPersonField;
import fr.gouv.recherche.scanr.db.repository.PersonRepository;
import fr.gouv.recherche.scanr.workflow.full.FullPersonProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component("PersonSimilarPersonProvider")
public class SimilarPersonProvider implements FullPersonProvider<List<SimilarTypedObject<Person>>> {

    public static final Set<FullPersonField> FIELDS = Sets.newHashSet();

    @Autowired
    private PersonRepository repository;

    @Override
    public boolean canProvide(FullPerson person) {
        return person.getSimilarPersons() != null && !person.getSimilarPersons().isEmpty();
    }

    @Override
    public List<SimilarTypedObject<Person>> computeField(FullPerson person) {
        List<SimilarTypedObject<Person>> personRelations = person.getSimilarPersons();

        List<String> idList = personRelations.stream().filter(it -> it.getTarget() != null).map(relation -> relation.getTarget().getId()).collect(Collectors.toList());
        List<Person> personsLight = repository.findByIdsLight(idList);

        // Put the light version of the person
        personsLight.stream().filter(Objects::nonNull).forEach(personLight -> {
            for (SimilarTypedObject<Person> personRelation : personRelations) {
                if (personRelation.getTarget() != null && personLight.getId().equals(personRelation.getTarget().getId())) {
                    personRelation.setTarget(personLight.getLightPerson());
                }
            }
        });

        return personRelations;
    }

    @Override
    public FullPersonField getField() {
        return FullPersonField.SIMILAR_PERSONS;
    }

    @Override
    public Set<FullPersonField> getDependencies() {
        return FIELDS;
    }
}
