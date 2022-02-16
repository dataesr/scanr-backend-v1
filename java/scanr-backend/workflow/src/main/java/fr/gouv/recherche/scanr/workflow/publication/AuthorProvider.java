/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.workflow.publication;

import com.google.common.collect.Sets;
import fr.gouv.recherche.scanr.db.model.Person;
import fr.gouv.recherche.scanr.db.model.Project;
import fr.gouv.recherche.scanr.db.model.full.FullPublication;
import fr.gouv.recherche.scanr.db.model.full.FullPublicationField;
import fr.gouv.recherche.scanr.db.model.publication.PublicationAuthorRelation;
import fr.gouv.recherche.scanr.db.repository.PersonRepository;
import fr.gouv.recherche.scanr.workflow.full.FullPublicationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Component("PublicationAuthorProvider")
public class AuthorProvider implements FullPublicationProvider<List<PublicationAuthorRelation>> {
    public static final Set<FullPublicationField> FIELDS = Sets.newHashSet();

    @Autowired
    private PersonRepository repository;

    @Override
    public boolean canProvide(FullPublication publication) {
        return publication.getAuthors() != null && !publication.getAuthors().isEmpty();
    }

    @Override
    public List<PublicationAuthorRelation> computeField(FullPublication publication) {

        publication.getAuthors().stream().filter(relation -> relation.getPerson() != null).forEach(publicationAuthorRelation -> {
            if (publicationAuthorRelation.getPerson().getId() != null) {
                Person person = repository.findOne(publicationAuthorRelation.getPerson().getId());
                if (person != null) {
                    publicationAuthorRelation.setPerson(person.getLightPerson());
                }
            }
        });
        return publication.getAuthors();
    }

    @Override
    public FullPublicationField getField() {
        return FullPublicationField.AUTHORS;
    }

    @Override
    public Set<FullPublicationField> getDependencies() {
        return FIELDS;
    }
}
