/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */

package fr.gouv.recherche.scanr.workflow.project;

import com.google.common.collect.Sets;
import fr.gouv.recherche.scanr.db.model.Project;
import fr.gouv.recherche.scanr.db.model.SimilarTypedObject;
import fr.gouv.recherche.scanr.db.model.full.FullProject;
import fr.gouv.recherche.scanr.db.model.full.FullProjectField;
import fr.gouv.recherche.scanr.db.repository.ProjectRepository;
import fr.gouv.recherche.scanr.workflow.full.FullProjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component("ProjectSimilarProjectProvider")
public class SimilarProjectProvider implements FullProjectProvider<List<SimilarTypedObject<Project>>> {

    public static final Set<FullProjectField> FIELDS = Sets.newHashSet();

    @Autowired
    private ProjectRepository repository;

    @Override
    public boolean canProvide(FullProject project) {
        return project.getSimilarProjects() != null && !project.getSimilarProjects().isEmpty();
    }

    @Override
    public List<SimilarTypedObject<Project>> computeField(FullProject project) {
        List<SimilarTypedObject<Project>> similarProjects = project.getSimilarProjects();

        List<Project> projectsLight = repository.findByIdsLight(similarProjects.stream().filter(relation -> relation.getTarget() != null).map(relation -> relation.getTarget().getId()).collect(Collectors.toList()));

        // Put the light version of the project
        projectsLight.stream().filter(Objects::nonNull).forEach(projectLight -> {
            for (SimilarTypedObject<Project> similarProject : similarProjects) {
                if (similarProject.getTarget() != null && projectLight.getId().equals(similarProject.getTarget().getId())) {
                    similarProject.setTarget(projectLight.getLightProject());
                }
            }
        });

        return similarProjects;
    }

    @Override
    public FullProjectField getField() {
        return FullProjectField.SIMILAR_PROJECTS;
    }

    @Override
    public Set<FullProjectField> getDependencies() {
        return FIELDS;
    }
}
