/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.workflow.structure;

import com.google.common.collect.Sets;
import fr.gouv.recherche.scanr.db.model.Structure;
import fr.gouv.recherche.scanr.db.model.StructureRelation;
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


@Component("StructureRelationProvider")
public class RelationProvider implements FullStructureProvider<List<StructureRelation>> {
    public static final Set<FullStructureField> FIELDS = Sets.newHashSet();

    @Autowired
    private StructureRepository repository;

    @Override
    public boolean canProvide(FullStructure structure) {
        return structure.getRelations() != null && !structure.getRelations().isEmpty();
    }

    @Override
    public List<StructureRelation> computeField(FullStructure structure) {
        List<StructureRelation> relationRelations = structure.getRelations();

        List<Structure> relationStructuresLight = repository.findByIdsLight(
                relationRelations
                        .stream()
                        .filter(relation -> !Objects.isNull(relation.getStructure()))
                        .map(relation -> relation.getStructure().getId())
                        .collect(Collectors.toList())
        );

        // Put the light version of the Structure
        relationStructuresLight.stream().filter(Objects::nonNull).forEach(relationStructureLight -> {
            for (StructureRelation relationRelation : relationRelations) {
                if (relationRelation.getStructure() != null && relationStructureLight.getId().equals(relationRelation.getStructure().getId())) {
                    relationRelation.setStructure(relationStructureLight.getLightStructure());
                }
            }
        });

        return relationRelations;
    }

    @Override
    public FullStructureField getField() {
        return FullStructureField.RELATIONS;
    }

    @Override
    public Set<FullStructureField> getDependencies() {
        return FIELDS;
    }
}
