/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.workflow.structure;

import com.google.common.collect.Sets;
import fr.gouv.recherche.scanr.db.model.Structure;
import fr.gouv.recherche.scanr.db.model.full.FullStructure;
import fr.gouv.recherche.scanr.db.model.full.FullStructureField;
import fr.gouv.recherche.scanr.db.model.full.StructureChildInverseRelation;
import fr.gouv.recherche.scanr.db.repository.StructureRepository;
import fr.gouv.recherche.scanr.workflow.full.FullStructureProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;


@Component("StructureChildrenProvider")
public class ChildrenProvider implements FullStructureProvider<List<StructureChildInverseRelation>> {
    public static final Set<FullStructureField> FIELDS = Sets.newHashSet();

    @Autowired
    private StructureRepository repository;

    @Override
    public boolean canProvide(FullStructure company) {
        return false;
    }

    @Override
    public List<StructureChildInverseRelation> computeField(FullStructure structure) {
        List<StructureChildInverseRelation> relations = repository.findByParentIdLight(structure.getId()).stream().map(StructureChildInverseRelation::new).collect(Collectors.toList());

        // Put the light version of the Structure
        relations
                .stream()
                .filter(relation -> !Objects.isNull(relation.getStructure()))
                .forEach(
                        relation -> {
                            Structure lightStructure = relation.getStructure().getLightStructure();
                            relation.setStructure(lightStructure);
                        }
                );
        return relations;
    }

    @Override
    public FullStructureField getField() {
        return FullStructureField.CHILDREN;
    }

    @Override
    public Set<FullStructureField> getDependencies() {
        return FIELDS;
    }
}
