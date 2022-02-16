/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.workflow.structure;

import com.google.common.collect.Sets;
import fr.gouv.recherche.scanr.db.model.Project;
import fr.gouv.recherche.scanr.db.model.Structure;
import fr.gouv.recherche.scanr.db.model.full.*;
import fr.gouv.recherche.scanr.db.model.publication.Publication;
import fr.gouv.recherche.scanr.db.repository.FullProjectRepository;
import fr.gouv.recherche.scanr.db.repository.ProjectRepository;
import fr.gouv.recherche.scanr.db.repository.PublicationRepository;
import fr.gouv.recherche.scanr.db.repository.StructureRepository;
import fr.gouv.recherche.scanr.workflow.full.FullStructureProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Provider to compute the graph of structures attached to this existing structure
 */
@Component("StructureGraphProvider")
public class GraphProvider implements FullStructureProvider<List<GraphElement>> {
    public static final Set<FullStructureField> FIELDS = Sets.newHashSet(FullStructureField.PROJECTS, FullStructureField.PUBLICATIONS);

    @Autowired
    private StructureRepository structureRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private PublicationRepository publicationRepository;

    @Override
    public List<GraphElement> computeField(FullStructure structure) {
        // Map structureId => Graph Element
        final Map<String, GraphElement> graph = new HashMap<>();

        // Projects
        try {
            if (structure.getProjects() != null) {
                List<Project> projects = projectRepository.findProjectByStructureId(structure.getId());
                for (Project project : projects) {
                    if (!Objects.isNull(project.getParticipants())) {
                        project.getParticipants().stream()
                                .filter(it -> !Objects.isNull(it.getStructure()))
                                .filter(it -> !Objects.isNull(it.getStructure().getId()))
                                .forEach(s -> addToGraphLink(graph, GraphElementTypeConstant.PROJECT, s.getStructure().getId()));
                    }
                }
            }
        } catch (Exception e) {
            // TODO: log error
        }

        // Publications
        List<Publication> publications = publicationRepository.findPublicationByStructureId(structure.getId());
        if (publications != null) {
            for (Publication publication : publications) {
                String graphElementType = publication.getProductionType();
                publication.getAffiliations().forEach(s -> addToGraphLink(graph, graphElementType, s.getId()));
            }
        }

        // now remove itself from the graph :-)
        graph.remove(structure.getId());

        // Package the result
        List<GraphElement> result = new ArrayList<>();
        Map<String, Structure> lightStructureMap = structureRepository.findByIdsLight(graph.keySet()).stream().collect(Collectors.toMap(Structure::getId, Structure::getLightStructure));
        for (String key : graph.keySet()) {
            GraphElement graphElement = graph.get(key);
            Structure s = lightStructureMap.get(key);
            // structure is absent for some reason, ignore it
            if (s == null) {
                continue;
            }
            graphElement.setStructure(s);
            result.add(graphElement);
        }

        // order the graph (DESC from weight)
        result.sort((s1, s2) -> -Integer.compare(s1.getWeight(), s2.getWeight()));

        return result;
    }

    private void addToGraphLink(Map<String, GraphElement> graph, String type, String structureId) {
        graph.putIfAbsent(structureId, new GraphElement());
        graph.get(structureId).addElement(type);
    }

    @Override
    public FullStructureField getField() {
        return FullStructureField.GRAPH;
    }

    @Override
    public Set<FullStructureField> getDependencies() {
        return FIELDS;
    }
}
