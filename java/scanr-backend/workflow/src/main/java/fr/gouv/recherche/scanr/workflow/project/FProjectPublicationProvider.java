/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */

package fr.gouv.recherche.scanr.workflow.project;

import com.google.common.collect.Sets;
import fr.gouv.recherche.scanr.db.model.full.FullProject;
import fr.gouv.recherche.scanr.db.model.full.FullProjectField;
import fr.gouv.recherche.scanr.db.model.publication.Publication;
import fr.gouv.recherche.scanr.db.repository.PublicationRepository;
import fr.gouv.recherche.scanr.workflow.full.FullProjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component("FProjectPublicationProvider")
public class FProjectPublicationProvider implements FullProjectProvider<List<Publication>> {
    public static final Set<FullProjectField> FIELDS = Sets.newHashSet();

    @Autowired
    private PublicationRepository repository;

    @Override
    public boolean canProvide(FullProject project) {
        return project != null && project.getId() != null;
    }

    @Override
    public List<Publication> computeField(FullProject project) {

        List<Publication> relations = repository.findPublicationByProjectId(project.getId());
        relations = relations.stream().map(Publication::getLightPublication).collect(Collectors.toList());

        return relations;
    }

    @Override
    public FullProjectField getField() {
        return FullProjectField.PUBLICATIONS;
    }

    @Override
    public Set<FullProjectField> getDependencies() {
        return FIELDS;
    }
}
