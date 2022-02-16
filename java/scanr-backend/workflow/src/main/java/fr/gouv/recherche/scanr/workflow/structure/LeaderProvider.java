/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.workflow.structure;

import com.google.common.collect.Sets;
import fr.gouv.recherche.scanr.db.model.Person;
import fr.gouv.recherche.scanr.db.model.PersonRelation;
import fr.gouv.recherche.scanr.db.model.full.FullStructure;
import fr.gouv.recherche.scanr.db.model.full.FullStructureField;
import fr.gouv.recherche.scanr.db.repository.PersonRepository;
import fr.gouv.recherche.scanr.workflow.full.FullStructureProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component("StructureLeaderProvider")
public class LeaderProvider implements FullStructureProvider<List<PersonRelation>> {
    public static final Set<FullStructureField> FIELDS = Sets.newHashSet();

    @Autowired
    private PersonRepository repository;

    @Override
    public boolean canProvide(FullStructure structure) {
        return structure.getLeaders() != null && !structure.getLeaders().isEmpty();
    }

    @Override
    public List<PersonRelation> computeField(FullStructure structure) {
        List<PersonRelation> leadersRelations = structure.getLeaders();

        List<Person> leaders = repository
                .findByIdsLight(
                        leadersRelations
                                .stream()
                                .filter(it -> !Objects.isNull(it.getPerson()) && !Objects.isNull(it.getPerson().getId()))
                                .map(relation -> relation.getPerson().getId()).collect(Collectors.toList()));

        // Put the light version of the Person
        leaders.stream().filter(Objects::nonNull).forEach(leader -> {
            for (PersonRelation leaderRelation : leadersRelations) {
                if (leaderRelation.getPerson() != null && leader.getId().equals(leaderRelation.getPerson().getId())) {
                    leaderRelation.setPerson(leader.getUltraLightPerson());
                }
            }
        });

        return leadersRelations;
    }

    @Override
    public FullStructureField getField() {
        return FullStructureField.LEADERS;
    }

    @Override
    public Set<FullStructureField> getDependencies() {
        return FIELDS;
    }
}