/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.workflow.structure;

import com.google.common.collect.Sets;
import fr.gouv.recherche.scanr.db.model.full.FullStructure;
import fr.gouv.recherche.scanr.db.model.full.FullStructureField;
import fr.gouv.recherche.scanr.db.model.full.StructurePublicationInverseRelation;
import fr.gouv.recherche.scanr.db.model.publication.Publication;
import fr.gouv.recherche.scanr.db.repository.PublicationRepository;
import fr.gouv.recherche.scanr.workflow.full.FullStructureProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Component("StructurePublicationProvider")
public class PublicationProvider implements FullStructureProvider<List<StructurePublicationInverseRelation>> {
    public static final Set<FullStructureField> FIELDS = Sets.newHashSet();

    @Autowired
    private PublicationRepository repository;

    @Override
    public boolean canProvide(FullStructure structure) {
        return structure != null && structure.getId() != null;
    }

    @Override
    public List<StructurePublicationInverseRelation> computeField(FullStructure structure) {

        List<StructurePublicationInverseRelation> publicationsRelations = new ArrayList<>();
        List<Publication> publications = repository.findPublicationUltraLightByStructureId(
                structure.getId(),
                new PageRequest(0, 50000)
        );

        if (!publications.isEmpty()) {
            // Put the light version of the Publication
            publications.stream().filter(Objects::nonNull).forEach(publication -> {
                StructurePublicationInverseRelation inverseRelation = new StructurePublicationInverseRelation();
                inverseRelation.setPublication(publication);
                publicationsRelations.add(inverseRelation);
            });
        }

        return publicationsRelations;
    }

    @Override
    public FullStructureField getField() {
        return FullStructureField.PUBLICATIONS;
    }

    @Override
    public Set<FullStructureField> getDependencies() {
        return FIELDS;
    }
}
