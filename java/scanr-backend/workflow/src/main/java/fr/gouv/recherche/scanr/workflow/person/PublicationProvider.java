/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */

package fr.gouv.recherche.scanr.workflow.person;

import com.google.common.collect.Sets;
import fr.gouv.recherche.scanr.db.model.full.FullPerson;
import fr.gouv.recherche.scanr.db.model.full.FullPersonField;
import fr.gouv.recherche.scanr.db.model.full.FullPersonPublication;
import fr.gouv.recherche.scanr.db.model.publication.Publication;
import fr.gouv.recherche.scanr.db.model.publication.PublicationAuthorRelation;
import fr.gouv.recherche.scanr.db.repository.PublicationRepository;
import fr.gouv.recherche.scanr.workflow.full.FullPersonProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component("PersonPublicationProvider")
public class PublicationProvider implements FullPersonProvider<List<FullPersonPublication>> {
    public static final Set<FullPersonField> FIELDS = Sets.newHashSet();

    @Autowired
    private PublicationRepository repository;

    @Override
    public boolean canProvide(FullPerson person) {
        return person != null && person.getId() != null && person.getPublications() != null;
    }

    @Override
    public List<FullPersonPublication> computeField(FullPerson person) {

        List<FullPersonPublication> personPublications = person.getPublications();

        List<Publication> personPublicationsLight = repository.findByIdsLight(
                personPublications
                        .stream()
                        .filter(relation -> !Objects.isNull(relation.getPublication()))
                        .map(relation -> relation.getPublication().getId())
                        .collect(Collectors.toList())
        );

        // Put the light version of the Structure
        personPublicationsLight.stream().filter(Objects::nonNull).forEach(personPublicationLight -> {
            for (FullPersonPublication personPublication : personPublications) {
                if (personPublication.getPublication().getAuthors() != null && !personPublication.getPublication().getAuthors().isEmpty()) {
                    for (PublicationAuthorRelation authorRelation : personPublication.getPublication().getAuthors()) {
                        if (authorRelation.getPerson() != null && Objects.equals(authorRelation.getPerson().getId(), person.getId())) {
                            personPublication.setRole(authorRelation.getRole());
                            break;
                        }
                    }
                }
                if (personPublication.getPublication() != null && personPublicationLight.getId().equals(personPublication.getPublication().getId())) {
                    personPublication.setPublication(personPublicationLight.getUltraLightPublication());
                }
            }
        });

        return personPublications;
    }

    @Override
    public FullPersonField getField() {
        return FullPersonField.PUBLICATIONS;
    }

    @Override
    public Set<FullPersonField> getDependencies() {
        return FIELDS;
    }
}
