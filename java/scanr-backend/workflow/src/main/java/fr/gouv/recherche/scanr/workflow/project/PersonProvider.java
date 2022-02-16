/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */

package fr.gouv.recherche.scanr.workflow.project;

import com.google.common.collect.Sets;
import fr.gouv.recherche.scanr.db.model.Person;
import fr.gouv.recherche.scanr.db.model.PersonRelation;
import fr.gouv.recherche.scanr.db.model.full.FullProject;
import fr.gouv.recherche.scanr.db.model.full.FullProjectField;
import fr.gouv.recherche.scanr.db.repository.PersonRepository;
import fr.gouv.recherche.scanr.workflow.full.FullProjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component("ProjectPersonProvider")
public class PersonProvider implements FullProjectProvider<List<PersonRelation>> {

    public static final Set<FullProjectField> FIELDS = Sets.newHashSet();

    @Autowired
    private PersonRepository repository;

    @Override
    public boolean canProvide(FullProject project) {
        return project.getPersons() != null && !project.getPersons().isEmpty();
    }

    @Override
    public List<PersonRelation> computeField(FullProject project) {
        List<PersonRelation> personRelations = project.getPersons();

        List<Person> personsLight = repository.findByIdsLight(personRelations.stream().filter(it -> it.getPerson() != null).map(relation -> relation.getPerson().getId()).collect(Collectors.toList()));

        // Put the light version of the person
        personsLight.stream().filter(Objects::nonNull).forEach(personLight -> {
            for (PersonRelation personRelation : personRelations) {
                if (personRelation.getPerson() != null && personLight.getId().equals(personRelation.getPerson().getId())) {
                    personRelation.setPerson(personLight.getLightPerson());
                }
            }
        });

        return personRelations;
    }

    @Override
    public FullProjectField getField() {
        return FullProjectField.PERSONS;
    }

    @Override
    public Set<FullProjectField> getDependencies() {
        return FIELDS;
    }
}
