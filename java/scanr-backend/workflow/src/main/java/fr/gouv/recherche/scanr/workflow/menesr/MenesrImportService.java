/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.workflow.menesr;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.gouv.recherche.scanr.companies.workflow.service.QueueService;
import fr.gouv.recherche.scanr.db.model.*;
import fr.gouv.recherche.scanr.db.model.full.*;
import fr.gouv.recherche.scanr.db.model.publication.Publication;
import fr.gouv.recherche.scanr.db.repository.PersonRepository;
import fr.gouv.recherche.scanr.db.repository.ProjectRepository;
import fr.gouv.recherche.scanr.db.repository.PublicationRepository;
import fr.gouv.recherche.scanr.db.repository.StructureRepository;
import fr.gouv.recherche.scanr.util.RepositoryLock;
import fr.gouv.recherche.scanr.workflow.full.*;
import fr.gouv.recherche.scanr.workflow.search.IndexPersonProcess;
import fr.gouv.recherche.scanr.workflow.search.IndexProjectProcess;
import fr.gouv.recherche.scanr.workflow.search.IndexPublicationProcess;
import fr.gouv.recherche.scanr.workflow.search.IndexStructureProcess;
import fr.gouv.recherche.scanr.workflow.website.WebsiteAnalysisService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Import service
 */
@Service
public class MenesrImportService {
    private static final Logger log = LoggerFactory.getLogger(MenesrImportService.class);

    private RepositoryLock<Structure, String, StructureRepository> structureRepository;

    private RepositoryLock<Project, String, ProjectRepository> projectRepository;

    private RepositoryLock<Publication, String, PublicationRepository> publicationRepository;

    private RepositoryLock<Person, String, PersonRepository> personRepository;

    @Autowired
    private WebsiteAnalysisService analysisService;

    @Autowired
    private MenesrFetcher fetcher;

    @Autowired
    private QueueService queueService;

    public int fetchStructures() {
        ObjectMapper mapper = new ObjectMapper();
        Set<String> idsToUpdate = new HashSet<>();
        Set<String> idsToDelete = new HashSet<>();
        Set<Link> linksToAnalyze = new HashSet<>();

        Structure structure = null;

        try {
            String filePath = fetcher.config.getResourceFilePath(FullStructure.class);
            JsonParser jsonParser = mapper.getFactory().createParser(new File(filePath));
            jsonParser.nextToken();
            while (jsonParser.nextToken() == JsonToken.START_OBJECT) {
                structure = mapper.readValue(jsonParser, FullStructure.class);
                idsToUpdate.add(structure.getId());

                if (structure.getLinks() != null) {
                    structure.getLinks().forEach(Link::computeId);
                }
                Set<Link> structureLinksToAnalyze = Objects.requireNonNull(structure.getLinks()).stream().filter(it -> it.getId() != null).collect(Collectors.toSet());
                linksToAnalyze.addAll(structureLinksToAnalyze);

                Structure finalStructure = structure;
                structureRepository.updateGlobally(structureRepository -> {
                    structureRepository.save(finalStructure);
                });
            }
        }
        catch(IllegalAccessException | InstantiationException | IOException e) {
            log.debug(e.getMessage());
            log.debug(String.valueOf(structure));
        }

        structureRepository.updateGlobally(structureRepository -> {
            // Deleted ids are ids that are present before but not anymore
            structureRepository.streamAllIds().forEach(idsToDelete::add);
            idsToDelete.removeAll(idsToUpdate);

            // Save all structures in the repository
            // Delete is here to prevent phantom entries
            log.info("[{}] Removing structures from Mongo", StringUtils.join(idsToDelete, ","));
            structureRepository.deleteByIds(idsToDelete);
        });

        for (String id : idsToDelete) {
            log.info("[{}] Creating message for deleting structure in Rabbit", id);
            queueService.push(id, IndexStructureProcess.QUEUE, null);
        }

        for (Link link : linksToAnalyze) {
            if (Link.MAIN_TYPE.equals(link.getType())) {
                CrawlMode mode = link.getMode();
                if (mode == null) {
                    mode = inferCrawlMode(link.getId());
                }
                analysisService.analyze(link.getUrl(), mode, false);
            }
        }

        return idsToUpdate.size();
    }

    public static CrawlMode inferCrawlMode(String link) {
        if (link.contains("?")) {
            return CrawlMode.SINGLE_PAGE;
        }
        if (!link.contains("/")) {
            return CrawlMode.FULL_DOMAIN;
        }
        String page = link.substring(link.lastIndexOf('/'));
        if (page.contains(".")) {
            return CrawlMode.SINGLE_PAGE;
        }
        return CrawlMode.SUBPATH;
    }

    public int fetchProjects() {
        ObjectMapper mapper = new ObjectMapper();
        Set<String> idsToUpdate = new HashSet<>();
        Set<String> idsToDelete = new HashSet<>();

        Project project = null;

        try {
            String filePath = fetcher.config.getResourceFilePath(FullProject.class);
            JsonParser jsonParser = mapper.getFactory().createParser(new File(filePath));
            jsonParser.nextToken();
            while (jsonParser.nextToken() == JsonToken.START_OBJECT) {
                project = mapper.readValue(jsonParser, FullProject.class);
                idsToUpdate.add(project.getId());

                Project finalProject = project;
                projectRepository.updateGlobally(projectRepository -> {
                    projectRepository.save(finalProject);
                });
            }
        }
        catch(IllegalAccessException | InstantiationException | IOException e) {
            log.debug(e.getMessage());
            log.debug(String.valueOf(project));
        }

        projectRepository.updateGlobally(projectRepository -> {
            // Deleted ids are ids that are present before but not anymore
            projectRepository.streamAllIds().forEach(idsToDelete::add);
            idsToDelete.removeAll(idsToUpdate);

            // Save all projects in the repository
            // Delete is here to prevent phantom entries
            log.info("[{}] Removing projects from Mongo", StringUtils.join(idsToDelete, ","));
            projectRepository.deleteByIds(idsToDelete);
        });

        for (String id : idsToDelete) {
            log.info("[{}] Creating message for deleting project in Rabbit", id);
            queueService.push(id, IndexProjectProcess.QUEUE, null);
        }

        return idsToUpdate.size();
    }


    public int fetchPublications() {
        ObjectMapper mapper = new ObjectMapper();
        Set<String> idsToUpdate = new HashSet<>();
        Set<String> idsToDelete = new HashSet<>();

        Publication publication = null;

        try {
            String filePath = fetcher.config.getResourceFilePath(FullPublication.class);
            JsonParser jsonParser = mapper.getFactory().createParser(new File(filePath));
            jsonParser.nextToken();
            while (jsonParser.nextToken() == JsonToken.START_OBJECT) {
                publication = mapper.readValue(jsonParser, FullPublication.class);
                idsToUpdate.add(publication.getId());

                Publication finalPublication = publication;
                publicationRepository.updateGlobally(publicationRepository -> {
                    publicationRepository.save(finalPublication);
                });
            }
        }
        catch(IllegalAccessException | InstantiationException | IOException e) {
            log.debug(e.getMessage());
            log.debug(String.valueOf(publication));
        }

        publicationRepository.updateGlobally(publicationRepository -> {
            // Deleted ids are ids that are present before but not anymore
            publicationRepository.streamAllIds().forEach(idsToDelete::add);
            idsToDelete.removeAll(idsToUpdate);

            // Save all publications in the repository
            // Delete is here to prevent phantom entries
            log.info("[{}] Removing publications from Mongo", StringUtils.join(idsToDelete, ","));
            publicationRepository.deleteByIds(idsToDelete);
        });

        for (String id : idsToDelete) {
            log.info("[{}] Creating message for deleting publication in Rabbit", id);
            queueService.push(id, IndexPublicationProcess.QUEUE, null);
        }

        return idsToUpdate.size();
    }

    public int fetchPersons() {
        ObjectMapper mapper = new ObjectMapper();
        Set<String> idsToUpdate = new HashSet<>();
        Set<String> idsToDelete = new HashSet<>();

        Person person = null;

        try {
            String filePath = fetcher.config.getResourceFilePath(FullPerson.class);
            JsonParser jsonParser = mapper.getFactory().createParser(new File(filePath));
            jsonParser.nextToken();
            while (jsonParser.nextToken() == JsonToken.START_OBJECT) {
                person = mapper.readValue(jsonParser, FullPerson.class);
                idsToUpdate.add(person.getId());

                Person finalPerson = person;
                personRepository.updateGlobally(personRepository -> {
                    personRepository.save(finalPerson);
                });
            }
        }
        catch(IllegalAccessException | InstantiationException | IOException e) {
            log.debug(e.getMessage());
        }

        personRepository.updateGlobally(personRepository -> {
            // Deleted ids are ids that are present before but not anymore
            personRepository.streamAllIds().forEach(idsToDelete::add);
            idsToDelete.removeAll(idsToUpdate);

            // Save all persons in the repository
            // Delete is here to prevent phantom entries
            log.info("[{}] Removing persons from Mongo", StringUtils.join(idsToDelete, ","));
            personRepository.deleteByIds(idsToDelete);
        });

        for (String id : idsToDelete) {
            log.info("[{}] Creating message for deleting person in Rabbit", id);
            queueService.push(id, IndexPersonProcess.QUEUE, null);
        }

        return idsToUpdate.size();
    }

    @Autowired
    public void setProjectRepository(ProjectRepository projectRepository) {
        this.projectRepository = RepositoryLock.get(projectRepository);
    }

    @Autowired
    public void setStructureRepository(StructureRepository structureRepository) {
        this.structureRepository = RepositoryLock.get(structureRepository);
    }

    @Autowired
    public void setPublicationRepository(PublicationRepository publicationRepository) {
        this.publicationRepository = RepositoryLock.get(publicationRepository);
    }

    @Autowired
    public void setPersonRepository(PersonRepository personRepository) {
        this.personRepository = RepositoryLock.get(personRepository);
    }
}
