/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */

package fr.gouv.recherche.scanr.workflow.structure;

import com.google.common.collect.Sets;
import fr.gouv.recherche.scanr.db.model.Structure;
import fr.gouv.recherche.scanr.db.model.StructureSpinoff;
import fr.gouv.recherche.scanr.db.model.full.FullStructure;
import fr.gouv.recherche.scanr.db.model.full.FullStructureField;
import fr.gouv.recherche.scanr.db.model.full.SpinoffFromInverseRelation;
import fr.gouv.recherche.scanr.db.repository.StructureRepository;
import fr.gouv.recherche.scanr.workflow.full.FullStructureProvider;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Component("SpinoffFromProvider")
public class SpinoffFromProvider implements FullStructureProvider<List<SpinoffFromInverseRelation>> {
    public static final Set<FullStructureField> FIELDS = Sets.newHashSet();

    @Autowired
    private StructureRepository repository;

    @Override
    public boolean canProvide(FullStructure structure) {
        return structure != null && !StringUtils.isEmpty(structure.getId());
    }

    @Override
    public List<SpinoffFromInverseRelation> computeField(FullStructure structure) {

        List<Structure> structures = repository.findStructureByStructureIdInSpinoff(structure.getId());
        List<SpinoffFromInverseRelation> inverseRelations = new ArrayList<>();

        for (Structure structureSpinoffFrom : structures) {
            SpinoffFromInverseRelation relation = new SpinoffFromInverseRelation();
            relation.setStructure(structureSpinoffFrom);

            for (StructureSpinoff spinoff : structureSpinoffFrom.getSpinoffs()) {
                if (!Objects.isNull(spinoff.getStructure()) && StringUtils.equals(structure.getId(), spinoff.getStructure().getId())) {
                    relation.setType(spinoff.getType());
                    break;
                }
            }

            inverseRelations.add(relation);
        }

        return inverseRelations;
    }

    @Override
    public FullStructureField getField() {
        return FullStructureField.SPINOFF_FROM;
    }

    @Override
    public Set<FullStructureField> getDependencies() {
        return FIELDS;
    }
}
