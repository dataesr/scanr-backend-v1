/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */

package fr.gouv.recherche.scanr.workflow.project;

import com.google.common.collect.Sets;
import fr.gouv.recherche.scanr.db.model.ProjectStructureRelation;
import fr.gouv.recherche.scanr.db.model.Structure;
import fr.gouv.recherche.scanr.db.model.full.FullProject;
import fr.gouv.recherche.scanr.db.model.full.FullProjectField;
import fr.gouv.recherche.scanr.db.repository.StructureRepository;
import fr.gouv.recherche.scanr.workflow.full.FullProjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component("ProjectParticipantsProvider")
public class ParticipantsProvider implements FullProjectProvider<List<ProjectStructureRelation>> {

    public static final Set<FullProjectField> FIELDS = Sets.newHashSet();

    @Autowired
    private StructureRepository repository;

    @Override
    public boolean canProvide(FullProject project) {
        return project.getParticipants() != null && !project.getParticipants().isEmpty();
    }

    @Override
    public List<ProjectStructureRelation> computeField(FullProject project) {
        List<ProjectStructureRelation> projectStructureRelations = project.getParticipants();

        List<String> idsList = projectStructureRelations.stream().filter(relation -> relation.getStructure() != null).map(relation -> relation.getStructure().getId()).collect(Collectors.toList());
        List<Structure> structuresLight = repository.findByIdsLight(idsList);

        // Put the light version of the Structure
        structuresLight.stream().filter(Objects::nonNull).forEach(structureLight -> {
            for (ProjectStructureRelation projectStructureRelation : projectStructureRelations) {
                if (projectStructureRelation.getStructure() != null && structureLight.getId().equals(projectStructureRelation.getStructure().getId())) {
                    projectStructureRelation.setStructure(structureLight.getLightStructure());
                }
            }
        });

        return projectStructureRelations;
    }

    @Override
    public FullProjectField getField() {
        return FullProjectField.PARTICIPANTS;
    }

    @Override
    public Set<FullProjectField> getDependencies() {
        return FIELDS;
    }
}
