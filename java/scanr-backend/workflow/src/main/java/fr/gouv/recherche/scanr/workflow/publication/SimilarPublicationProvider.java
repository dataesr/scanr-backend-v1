/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.workflow.publication;

import com.google.common.collect.Sets;
import fr.gouv.recherche.scanr.db.model.SimilarTypedObject;
import fr.gouv.recherche.scanr.db.model.full.FullPublication;
import fr.gouv.recherche.scanr.db.model.full.FullPublicationField;
import fr.gouv.recherche.scanr.db.model.publication.Publication;
import fr.gouv.recherche.scanr.db.repository.PublicationRepository;
import fr.gouv.recherche.scanr.workflow.full.FullPublicationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;


@Component("PublicationSimilarPublicationProvider")
public class SimilarPublicationProvider implements FullPublicationProvider<List<SimilarTypedObject<Publication>>> {
    public static final Set<FullPublicationField> FIELDS = Sets.newHashSet();

    @Autowired
    private PublicationRepository repository;

    @Override
    public boolean canProvide(FullPublication publication) {
        return publication.getSimilarPublications() != null && !publication.getSimilarPublications().isEmpty();
    }

    @Override
    public List<SimilarTypedObject<Publication>> computeField(FullPublication publication) {
        List<SimilarTypedObject<Publication>> similarPublications = publication.getSimilarPublications();

        List<Publication> publicationsLight = repository.findByIdsLight(similarPublications.stream().filter(relation -> relation.getTarget() != null).map(relation -> relation.getTarget().getId()).collect(Collectors.toList()));

        // Put the light version of the Structure
        publicationsLight.stream().filter(Objects::nonNull).forEach(projectLight -> {
            for (SimilarTypedObject<Publication> similarProject : similarPublications) {
                if (similarProject.getTarget() != null && projectLight.getId().equals(similarProject.getTarget().getId())) {
                    similarProject.setTarget(projectLight.getLightPublication());
                }
            }
        });

        return similarPublications;
    }

    @Override
    public FullPublicationField getField() {
        return FullPublicationField.SIMILAR_PUBLICATIONS;
    }

    @Override
    public Set<FullPublicationField> getDependencies() {
        return FIELDS;
    }
}
