/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.workflow.publication;

import com.google.common.collect.Sets;
import fr.gouv.recherche.scanr.db.model.Project;
import fr.gouv.recherche.scanr.db.model.full.FullPublication;
import fr.gouv.recherche.scanr.db.model.full.FullPublicationField;
import fr.gouv.recherche.scanr.db.repository.ProjectRepository;
import fr.gouv.recherche.scanr.workflow.full.FullPublicationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Component("PublicationFPProjectProvider")
public class FPProjectProvider implements FullPublicationProvider<List<Project>> {
    public static final Set<FullPublicationField> FIELDS = Sets.newHashSet();

    @Autowired
    private ProjectRepository repository;

    @Override
    public boolean canProvide(FullPublication publication) {
        return publication.getProjects() != null && !publication.getProjects().isEmpty();
    }

    @Override
    public List<Project> computeField(FullPublication publication) {

        List<String> personsId = publication.getProjects().stream().map(Project::getId).collect(Collectors.toList());
        return ((List<Project>) repository.findAll(personsId)).stream().map(Project::getLightProject).collect(Collectors.toList());
    }

    @Override
    public FullPublicationField getField() {
        return FullPublicationField.PROJECTS;
    }

    @Override
    public Set<FullPublicationField> getDependencies() {
        return FIELDS;
    }
}
