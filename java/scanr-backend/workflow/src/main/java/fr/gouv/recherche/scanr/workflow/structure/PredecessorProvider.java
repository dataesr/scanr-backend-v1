/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.workflow.structure;

import com.google.common.collect.Sets;
import fr.gouv.recherche.scanr.db.model.Structure;
import fr.gouv.recherche.scanr.db.model.StructurePredecessorRelation;
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


@Component("StructurePredecessorProvider")
public class PredecessorProvider implements FullStructureProvider<List<StructurePredecessorRelation>> {
    public static final Set<FullStructureField> FIELDS = Sets.newHashSet();

    @Autowired
    private StructureRepository repository;

    @Override
    public boolean canProvide(FullStructure structure) {
        return structure.getPredecessors() != null && !structure.getPredecessors().isEmpty();
    }

    @Override
    public List<StructurePredecessorRelation> computeField(FullStructure structure) {
        List<StructurePredecessorRelation> predecessorRelations = structure.getPredecessors();

        List<Structure> predecessorStructuresLight = repository.findByIdsLight(
                predecessorRelations
                        .stream()
                        .filter(relation -> !Objects.isNull(relation.getStructure()))
                        .map(relation -> relation.getStructure().getId())
                        .collect(Collectors.toList())
        );

        // Put the light version of the Structure
        predecessorStructuresLight.stream().filter(Objects::nonNull).forEach(predecessorStructureLight -> {
            for (StructurePredecessorRelation predecessorRelation : predecessorRelations) {
                if (predecessorRelation.getStructure() != null && predecessorStructureLight.getId().equals(predecessorRelation.getStructure().getId())) {
                    predecessorRelation.setStructure(predecessorStructureLight.getLightStructure());
                }
            }
        });

        return predecessorRelations;
    }

    @Override
    public FullStructureField getField() {
        return FullStructureField.PREDECESSORS;
    }

    @Override
    public Set<FullStructureField> getDependencies() {
        return FIELDS;
    }
}
