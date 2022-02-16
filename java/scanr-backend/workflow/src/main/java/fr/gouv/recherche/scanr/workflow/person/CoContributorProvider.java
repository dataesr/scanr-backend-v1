/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */

package fr.gouv.recherche.scanr.workflow.person;

import com.google.common.collect.Sets;
import fr.gouv.recherche.scanr.db.model.Person;
import fr.gouv.recherche.scanr.db.model.full.FullPerson;
import fr.gouv.recherche.scanr.db.model.full.FullPersonField;
import fr.gouv.recherche.scanr.db.repository.PersonRepository;
import fr.gouv.recherche.scanr.workflow.full.FullPersonProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component("PersonCoContributorProvider")
public class CoContributorProvider implements FullPersonProvider<List<Person>> {
    public static final Set<FullPersonField> FIELDS = Sets.newHashSet();

    @Autowired
    private PersonRepository repository;

    @Override
    public boolean canProvide(FullPerson person) {
        return person.getCoContributors() != null && !person.getCoContributors().isEmpty();
    }

    @Override
    public List<Person> computeField(FullPerson person) {

        List<String> personsId = person.getCoContributors().stream().map(Person::getId).collect(Collectors.toList());
        return ((List<Person>) repository.findAll(personsId)).stream().map(Person::getUltraLightPerson).collect(Collectors.toList());
    }

    @Override
    public FullPersonField getField() {
        return FullPersonField.CO_CONTRIBUTORS;
    }

    @Override
    public Set<FullPersonField> getDependencies() {
        return FIELDS;
    }
}
