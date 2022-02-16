/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.api;


import com.google.common.collect.ImmutableMap;
import fr.gouv.recherche.scanr.api.util.ApiConstants;
import fr.gouv.recherche.scanr.companies.workflow.service.scheduler.QueueScheduler;
import fr.gouv.recherche.scanr.db.model.full.FullStructureField;
import fr.gouv.recherche.scanr.db.repository.FullStructureRepository;
import fr.gouv.recherche.scanr.db.repository.StructureRepository;
import fr.gouv.recherche.scanr.workflow.full.FullStructureService;
import fr.gouv.recherche.scanr.workflow.menesr.MenesrImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

import static fr.gouv.recherche.scanr.api.util.ApiConstants.PRODUCES_JSON;

/**
 * Temporary Class (to be removed) to create structure and index them
 */
@Controller
@RequestMapping("/admin/import")
public class ImportMenesrApi {
    @Autowired
    private MenesrImportService service;
    @Autowired
    private FullStructureRepository repository;
    @Autowired
    private FullStructureService fsservice;

    @Autowired
    private StructureRepository structureRepository;

    @Autowired
    private QueueScheduler queueScheduler;


    @ResponseBody
    @RequestMapping(value = "/structures", method = RequestMethod.GET, produces = PRODUCES_JSON)
    public int fetchStructures() {
        return service.fetchStructures();
    }


    @RequestMapping(value = "/refresh", method = RequestMethod.POST, produces = ApiConstants.PRODUCES_JSON)
    @ResponseBody
    public long refresh(@RequestParam FullStructureField field) {
        return repository.selectAllIds().peek(id -> fsservice.refresh(id, field)).count();
    }

    @RequestMapping(value = "/ensureCreated", method = RequestMethod.POST, produces = ApiConstants.PRODUCES_JSON)
    @ResponseBody
    public long ensureCreated() {
        return structureRepository.streamAllIds().peek(id -> fsservice.ensureCreated(id)).count();
    }

    @ResponseBody
    @RequestMapping(value = "/projects", method = RequestMethod.GET, produces = PRODUCES_JSON)
    public int fetchProjects() {
        return service.fetchProjects();
    }

    @ResponseBody
    @RequestMapping(value = "/persons", method = RequestMethod.GET, produces = PRODUCES_JSON)
    public int fetchPersons() {
        return service.fetchPersons();
    }

    @ResponseBody
    @RequestMapping(value = "/publications", method = RequestMethod.GET, produces = PRODUCES_JSON)
    public int fetchPublications() {
        return service.fetchPublications();
    }

    @ResponseBody
    @RequestMapping(value = "/all", method = RequestMethod.GET, produces = PRODUCES_JSON)
    public Map<String, Integer> all() {
        return ImmutableMap.of("structures", fetchStructures(), "projects", fetchProjects(), "publications", fetchPublications(), "persons", fetchPersons());
    }

}
