/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */

package fr.gouv.recherche.scanr.workflow.person;

import com.google.common.collect.Sets;
import fr.gouv.recherche.scanr.db.model.Award;
import fr.gouv.recherche.scanr.db.model.Structure;
import fr.gouv.recherche.scanr.db.model.full.FullPerson;
import fr.gouv.recherche.scanr.db.model.full.FullPersonField;
import fr.gouv.recherche.scanr.db.repository.StructureRepository;
import fr.gouv.recherche.scanr.workflow.full.FullPersonProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component("AwardProvider")
public class AwardProvider implements FullPersonProvider<List<Award>> {

    public static final Set<FullPersonField> FIELDS = Sets.newHashSet();

    @Autowired
    private StructureRepository repository;

    @Override
    public boolean canProvide(FullPerson person) {
        return person.getAwards() != null && !person.getAwards().isEmpty();
    }

    @Override
    public List<Award> computeField(FullPerson person) {
        List<Award> personRelations = person.getAwards();

        List<String> idList = personRelations.stream().filter(it -> it.getStructure() != null).map(relation -> relation.getStructure().getId()).collect(Collectors.toList());
        List<Structure> personsLight = repository.findByIdsLight(idList);

        // Put the light version of the person
        personsLight.stream().filter(Objects::nonNull).forEach(personLight -> {
            for (Award personRelation : personRelations) {
                if (personRelation.getStructure() != null && personLight.getId().equals(personRelation.getStructure().getId())) {
                    personRelation.setStructure(personLight.getLightStructure());
                }
            }
        });

        return personRelations;
    }

    @Override
    public FullPersonField getField() {
        return FullPersonField.AWARDS;
    }

    @Override
    public Set<FullPersonField> getDependencies() {
        return FIELDS;
    }
}
