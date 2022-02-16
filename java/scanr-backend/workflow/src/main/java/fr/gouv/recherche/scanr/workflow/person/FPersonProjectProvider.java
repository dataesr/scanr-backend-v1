/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */

package fr.gouv.recherche.scanr.workflow.person;

import com.google.common.collect.Sets;
import fr.gouv.recherche.scanr.db.model.PersonRelation;
import fr.gouv.recherche.scanr.db.model.Project;
import fr.gouv.recherche.scanr.db.model.full.FullPerson;
import fr.gouv.recherche.scanr.db.model.full.FullPersonField;
import fr.gouv.recherche.scanr.db.model.full.FullPersonProject;
import fr.gouv.recherche.scanr.db.model.full.FullPersonPublication;
import fr.gouv.recherche.scanr.db.model.publication.Publication;
import fr.gouv.recherche.scanr.db.model.publication.PublicationAuthorRelation;
import fr.gouv.recherche.scanr.db.repository.ProjectRepository;
import fr.gouv.recherche.scanr.workflow.full.FullPersonProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component("FPersonProjectProvider")
public class FPersonProjectProvider implements FullPersonProvider<List<FullPersonProject>> {

    public static final Set<FullPersonField> FIELDS = Sets.newHashSet();

    @Autowired
    private ProjectRepository repository;

    @Override
    public boolean canProvide(FullPerson person) {
        return person != null && person.getId() != null;
    }

    @Override
    public List<FullPersonProject> computeField(FullPerson person) {

        List<FullPersonProject> personProjects = person.getProjects();

        List<Project> personProjectsLight = repository.findByIdsLight(
                personProjects
                        .stream()
                        .filter(relation -> !Objects.isNull(relation.getProject()))
                        .map(relation -> relation.getProject().getId())
                        .collect(Collectors.toList())
        );

        // Put the light version of the Project
        personProjectsLight.stream().filter(Objects::nonNull).forEach(personProjectLight -> {
            for (FullPersonProject personProject : personProjects) {
                if (personProject.getProject().getPersons() != null && !personProject.getProject().getPersons().isEmpty()) {
                    for (PersonRelation personRelation : personProject.getProject().getPersons()) {
                        if (personRelation.getPerson() != null && Objects.equals(personRelation.getPerson().getId(), person.getId())) {
                            personProject.setRole(personRelation.getRole());
                            break;
                        }
                    }
                }
                if (personProject.getProject() != null && personProjectLight.getId().equals(personProject.getProject().getId())) {
                    personProject.setProject(personProjectLight.getLightProject());
                }
            }
        });

        return personProjects;
    }

    @Override
    public FullPersonField getField() {
        return FullPersonField.PROJECTS;
    }

    @Override
    public Set<FullPersonField> getDependencies() {
        return FIELDS;
    }
}
