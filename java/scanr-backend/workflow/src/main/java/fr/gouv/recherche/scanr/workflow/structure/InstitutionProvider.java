/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.workflow.structure;

import com.google.common.collect.Sets;
import fr.gouv.recherche.scanr.db.model.InstitutionRelation;
import fr.gouv.recherche.scanr.db.model.Structure;
import fr.gouv.recherche.scanr.db.model.full.FullStructure;
import fr.gouv.recherche.scanr.db.model.full.FullStructureField;
import fr.gouv.recherche.scanr.db.repository.StructureRepository;
import fr.gouv.recherche.scanr.workflow.full.FullStructureProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component("StructureInstitutionProvider")
public class InstitutionProvider implements FullStructureProvider<List<InstitutionRelation>> {
    public static final Set<FullStructureField> FIELDS = Sets.newHashSet();

    @Autowired
    private StructureRepository repository;

    @Override
    public boolean canProvide(FullStructure structure) {
        return structure.getInstitutions() != null && !structure.getInstitutions().isEmpty();
    }

    @Override
    public List<InstitutionRelation> computeField(FullStructure structure) {
        List<InstitutionRelation> institutionRelations = structure.getInstitutions();

        List<Structure> insitutionStructuresLight = repository.findByIdsLight(
                institutionRelations
                        .stream()
                        .filter(relation -> !Objects.isNull(relation.getStructure()))
                        .map(relation -> relation.getStructure().getId())
                        .collect(Collectors.toList())
        );

        // Put the light version of the Structure
        insitutionStructuresLight.stream().filter(Objects::nonNull).forEach(institutionStructureLight -> {
            for (InstitutionRelation institutionRelation : institutionRelations) {
                if (institutionRelation.getStructure() != null && institutionStructureLight.getId().equals(institutionRelation.getStructure().getId())) {
                    institutionRelation.setStructure(institutionStructureLight.getLightStructure());
                }
            }
        });

        return institutionRelations;
    }

    @Override
    public FullStructureField getField() {
        return FullStructureField.INSTITUTIONS;
    }

    @Override
    public Set<FullStructureField> getDependencies() {
        return FIELDS;
    }
}
