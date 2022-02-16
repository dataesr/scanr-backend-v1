/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.workflow.structure;

import com.google.common.collect.Sets;
import fr.gouv.recherche.scanr.db.model.Project;
import fr.gouv.recherche.scanr.db.model.ProjectStructureRelation;
import fr.gouv.recherche.scanr.db.model.full.FullStructure;
import fr.gouv.recherche.scanr.db.model.full.FullStructureField;
import fr.gouv.recherche.scanr.db.model.full.StructureProjectInverseRelation;
import fr.gouv.recherche.scanr.db.repository.ProjectRepository;
import fr.gouv.recherche.scanr.workflow.full.FullStructureProvider;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Component("StructureProjectProvider")
public class ProjectProvider implements FullStructureProvider<List<StructureProjectInverseRelation>> {
    public static final Set<FullStructureField> FIELDS = Sets.newHashSet();

    @Autowired
    private ProjectRepository repository;

    @Override
    public boolean canProvide(FullStructure structure) {
        return structure != null && structure.getId() != null;
    }

    @Override
    public List<StructureProjectInverseRelation> computeField(FullStructure structure) {

        List<StructureProjectInverseRelation> projectsRelations = new ArrayList<>();
        List<Project> projects = repository.findProjectByStructureId(structure.getId());

        // Put the light version of the Project
        projects.stream().filter(Objects::nonNull).forEach(project -> {
            // Search our structure in participants
            for (ProjectStructureRelation structureRelation : project.getParticipants()) {
                if (!Objects.isNull(structureRelation.getStructure()) && StringUtils.equals(structureRelation.getStructure().getId(), structure.getId())) {
                    // Create the inverse relation
                    StructureProjectInverseRelation projectRelation = new StructureProjectInverseRelation();
                    projectRelation.setProject(project.getUltraLightProject());
                    projectRelation.setFunding(structureRelation.getFunding());
                    projectRelation.setRole(structureRelation.getRole());

                    projectsRelations.add(projectRelation);
                    break;
                }
            }
        });

        return projectsRelations;
    }

    @Override
    public FullStructureField getField() {
        return FullStructureField.PROJECTS;
    }

    @Override
    public Set<FullStructureField> getDependencies() {
        return FIELDS;
    }
}
