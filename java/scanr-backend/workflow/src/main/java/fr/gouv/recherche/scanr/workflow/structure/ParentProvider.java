/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.workflow.structure;

import com.google.common.collect.Sets;
import fr.gouv.recherche.scanr.db.model.Structure;
import fr.gouv.recherche.scanr.db.model.StructureParentRelation;
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


@Component("StructureParentProvider")
public class ParentProvider implements FullStructureProvider<List<StructureParentRelation>> {
    public static final Set<FullStructureField> FIELDS = Sets.newHashSet();

    @Autowired
    private StructureRepository repository;

    @Override
    public boolean canProvide(FullStructure structure) {
        return structure.getParents() != null && !structure.getParents().isEmpty();
    }

    @Override
    public List<StructureParentRelation> computeField(FullStructure structure) {
        List<StructureParentRelation> parentRelations = structure.getParents();

        List<Structure> parentStructuresLight = repository.findByIdsLight(
                parentRelations
                        .stream()
                        .filter(relation -> !Objects.isNull(relation.getStructure()))
                        .map(relation -> relation.getStructure().getId())
                        .collect(Collectors.toList())
        );

        // Put the light version of the Structure
        parentStructuresLight.stream().filter(Objects::nonNull).forEach(parentStructureLight -> {
            for (StructureParentRelation parentRelation : parentRelations) {
                if (parentRelation.getStructure() != null && parentStructureLight.getId().equals(parentRelation.getStructure().getId())) {
                    parentRelation.setStructure(parentStructureLight.getLightStructure());
                }
            }
        });

        return parentRelations;
    }

    @Override
    public FullStructureField getField() {
        return FullStructureField.PARENTS;
    }

    @Override
    public Set<FullStructureField> getDependencies() {
        return FIELDS;
    }
}
