/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.workflow.publication;

import com.google.common.collect.Sets;
import fr.gouv.recherche.scanr.db.model.full.FullPublication;
import fr.gouv.recherche.scanr.db.model.full.FullPublicationField;
import fr.gouv.recherche.scanr.db.model.publication.Publication;
import fr.gouv.recherche.scanr.db.repository.PublicationRepository;
import fr.gouv.recherche.scanr.workflow.full.FullPublicationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Component("PublicationCitedByProvider")
public class CitedByProvider implements FullPublicationProvider<List<Publication>> {
    public static final Set<FullPublicationField> FIELDS = Sets.newHashSet();

    @Autowired
    private PublicationRepository repository;

    @Override
    public boolean canProvide(FullPublication publication) {
        return publication.getCitedBy() != null && !publication.getCitedBy().isEmpty();
    }

    @Override
    public List<Publication> computeField(FullPublication publication) {

        List<String> personsId = publication.getCitedBy().stream().map(Publication::getId).collect(Collectors.toList());
        return ((List<Publication>) repository.findAll(personsId)).stream().map(Publication::getUltraLightPublication).collect(Collectors.toList());
    }

    @Override
    public FullPublicationField getField() {
        return FullPublicationField.CITED_BY;
    }

    @Override
    public Set<FullPublicationField> getDependencies() {
        return FIELDS;
    }
}
