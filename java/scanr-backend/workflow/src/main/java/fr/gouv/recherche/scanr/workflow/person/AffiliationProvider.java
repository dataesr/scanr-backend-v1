/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */

package fr.gouv.recherche.scanr.workflow.person;

import com.google.common.collect.Sets;
import fr.gouv.recherche.scanr.db.model.PublicationPersonAffiliation;
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

@Component("PersonAffiliationProvider")
public class AffiliationProvider implements FullPersonProvider<List<PublicationPersonAffiliation>> {

    public static final Set<FullPersonField> FIELDS = Sets.newHashSet();

    @Autowired
    private StructureRepository repository;

    @Override
    public boolean canProvide(FullPerson person) {
        return person.getAffiliations() != null && !person.getAffiliations().isEmpty();
    }

    @Override
    public List<PublicationPersonAffiliation> computeField(FullPerson person) {
        List<PublicationPersonAffiliation> affiliationRelations = person.getAffiliations();

        List<String> idList = affiliationRelations.stream().filter(it -> it.getStructure() != null).map(relation -> relation.getStructure().getId()).collect(Collectors.toList());
        List<Structure> structuresLight = repository.findByIdsLight(idList);

        // Put the light version of the person
        structuresLight.stream().filter(Objects::nonNull).forEach(structureLight -> {
            for (PublicationPersonAffiliation affiliationRelation : affiliationRelations) {
                if (affiliationRelation.getStructure() != null && structureLight.getId().equals(affiliationRelation.getStructure().getId())) {
                    affiliationRelation.setStructure(structureLight.getLightStructure());
                }
            }
        });

        return affiliationRelations;
    }

    @Override
    public FullPersonField getField() {
        return FullPersonField.AFFILIATIONS;
    }

    @Override
    public Set<FullPersonField> getDependencies() {
        return FIELDS;
    }
}
