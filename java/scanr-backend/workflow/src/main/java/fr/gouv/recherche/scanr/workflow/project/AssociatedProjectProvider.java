/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */

package fr.gouv.recherche.scanr.workflow.project;

import com.google.common.collect.Sets;
import fr.gouv.recherche.scanr.db.model.Project;
import fr.gouv.recherche.scanr.db.model.full.FullProject;
import fr.gouv.recherche.scanr.db.model.full.FullProjectField;
import fr.gouv.recherche.scanr.db.repository.ProjectRepository;
import fr.gouv.recherche.scanr.workflow.full.FullProjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component("ProjectAssociatedProjectProvider")
public class AssociatedProjectProvider implements FullProjectProvider<List<Project>> {

    public static final Set<FullProjectField> FIELDS = Sets.newHashSet();

    @Autowired
    private ProjectRepository repository;

    @Override
    public boolean canProvide(FullProject project) {
        return project.getAssociatedProjects() != null && !project.getAssociatedProjects().isEmpty();
    }

    @Override
    public List<Project> computeField(FullProject project) {

        List<String> personsId = project.getAssociatedProjects().stream().map(Project::getId).collect(Collectors.toList());
        return ((List<Project>) repository.findAll(personsId)).stream().map(Project::getLightProject).collect(Collectors.toList());
    }

    @Override
    public FullProjectField getField() {
        return FullProjectField.ASSOCIATED_PROJECTS;
    }

    @Override
    public Set<FullProjectField> getDependencies() {
        return FIELDS;
    }
}
