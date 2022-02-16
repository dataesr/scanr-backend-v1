/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.api;

import fr.gouv.recherche.scanr.db.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 */
@Controller
@RequestMapping("/services/counts")
public class CountApi {
    @Autowired
    private StructureRepository structure;
    @Autowired
    private WebsiteRepository website;
    @Autowired
    private FullStructureRepository fullStructure;
    @Autowired
    private ProjectRepository project;
    @Autowired
    private FullProjectRepository fullProjectRepository;
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private FullPersonRepository fullPersonRepository;
    @Autowired
    private PublicationRepository publicationRepository;
    @Autowired
    private FullPublicationRepository fullPublicationRepository;

    @ResponseBody
    @RequestMapping(value = "", method = RequestMethod.GET)
    public CountReport get() {
        return new CountReport(structure.count(), website.count(), fullStructure.count(), project.count(), fullProjectRepository.count(),
                personRepository.count(), fullPersonRepository.count(), publicationRepository.count(), fullPublicationRepository.count());
    }

    public static class CountReport {
        public long websites;
        public long structures;
        public long fullStructures;
        public long projects;
        public long fullProjects;
        public long persons;
        public long fullPersons;
        public long publications;
        public long fullPublications;

        public CountReport(long structures, long websites, long fullStructures, long projects, long fullProjects, long persons, long fullPersons, long publications, long fullPublications) {
            this.structures = structures;
            this.websites = websites;
            this.fullStructures = fullStructures;
            this.projects = projects;
            this.fullProjects = fullProjects;
            this.persons = persons;
            this.fullPersons = fullPersons;
            this.publications = publications;
            this.fullPublications = fullPublications;
        }
    }
}
