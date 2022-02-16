/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.api;


import fr.gouv.recherche.scanr.companies.workflow.service.QueueService;
import fr.gouv.recherche.scanr.db.repository.*;
import fr.gouv.recherche.scanr.workflow.search.*;
import org.elasticsearch.client.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import static fr.gouv.recherche.scanr.api.util.ApiConstants.PRODUCES_JSON;

/**
 * Temporary Class (to be removed) to create structure and index them
 */
@Controller
@RequestMapping("/admin/index")
public class IndexServiceApi {
    @Autowired
    private FullStructureRepository fullStructureRepository;
    @Autowired
    private StructureRepository structureRepository;

    @Autowired
    private FullProjectRepository fullProjectRepository;
    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private FullPublicationRepository fullPublicationRepository;
    @Autowired
    private PublicationRepository publicationRepository;

    @Autowired
    private FullPersonRepository fullPersonRepository;
    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private IndexUpdatedProcess indexUpdatedProcess;
    @Autowired
    private QueueService queueService;

    @ResponseBody
    @RequestMapping(value = "/all/structures", method = RequestMethod.GET, produces = PRODUCES_JSON)
    public long indexAllStructures() {
        return structureRepository.streamAllIds().peek(id -> queueService.push(id, IndexStructureProcess.QUEUE, null)).count();
    }

    @ResponseBody
    @RequestMapping(value = "/all/projects", method = RequestMethod.GET, produces = PRODUCES_JSON)
    public long indexAllProjects() {
        return projectRepository.streamAllIds().peek(id -> queueService.push(id, IndexProjectProcess.QUEUE, null)).count();
    }

    @ResponseBody
    @RequestMapping(value = "/all/publications", method = RequestMethod.GET, produces = PRODUCES_JSON)
    public long indexAllPublications() {
        return publicationRepository.streamAllIds().peek(id -> queueService.push(id, IndexPublicationProcess.QUEUE, null)).count();
    }

    @ResponseBody
    @RequestMapping(value = "/all/persons", method = RequestMethod.GET, produces = PRODUCES_JSON)
    public long indexAllPersons() {
        return personRepository.streamAllIds().peek(id -> queueService.push(id, IndexPersonProcess.QUEUE, null)).count();
    }

    @ResponseBody
    @RequestMapping(value = "/person/{id}", method = RequestMethod.GET, produces = PRODUCES_JSON)
    public void indexOnePerson(@PathVariable String id) {
        queueService.push(id, IndexPersonProcess.QUEUE, null);
    }

    @ResponseBody
    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = PRODUCES_JSON)
    public void indexOneStructures(@PathVariable String id) {
        queueService.push(id, IndexStructureProcess.QUEUE, null);
    }
}
