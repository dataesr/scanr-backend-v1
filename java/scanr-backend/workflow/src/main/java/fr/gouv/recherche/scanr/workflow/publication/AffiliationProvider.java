/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */

package fr.gouv.recherche.scanr.workflow.publication;

import com.google.common.collect.Sets;
import fr.gouv.recherche.scanr.db.model.Structure;
import fr.gouv.recherche.scanr.db.model.full.FullPublication;
import fr.gouv.recherche.scanr.db.model.full.FullPublicationField;
import fr.gouv.recherche.scanr.db.repository.StructureRepository;
import fr.gouv.recherche.scanr.workflow.full.FullPublicationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component("PublicationAffiliationProvider")
public class AffiliationProvider implements FullPublicationProvider<List<Structure>> {
    public static final Set<FullPublicationField> FIELDS = Sets.newHashSet();

    @Autowired
    private StructureRepository repository;

    @Override
    public boolean canProvide(FullPublication publication) {
        return publication.getAffiliations() != null && !publication.getAffiliations().isEmpty();
    }

    @Override
    public List<Structure> computeField(FullPublication publication) {

        List<String> structuresId = publication.getAffiliations().stream().map(Structure::getId).collect(Collectors.toList());
        return ((List<Structure>) repository.findAll(structuresId)).stream().map(Structure::getLightStructure).collect(Collectors.toList());
    }

    @Override
    public FullPublicationField getField() {
        return FullPublicationField.AFFILIATIONS;
    }

    @Override
    public Set<FullPublicationField> getDependencies() {
        return FIELDS;
    }
}
