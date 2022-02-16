/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.workflow.website;

import fr.gouv.recherche.scanr.db.model.Link;
import fr.gouv.recherche.scanr.db.model.Project;
import fr.gouv.recherche.scanr.db.model.Website;
import fr.gouv.recherche.scanr.db.model.full.FullStructure;
import fr.gouv.recherche.scanr.db.model.full.FullStructureField;
import fr.gouv.recherche.scanr.db.repository.WebsiteRepository;
import fr.gouv.recherche.scanr.workflow.full.FullStructureProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Component
public class WebsiteProvider implements FullStructureProvider<List<Website>> {
//    public static final Set<FullStructureField> FIELDS = Sets.newHashSet(FullStructureField.STRUCTURE);

    @Autowired
    private WebsiteRepository repository;

    @Override
    public boolean canProvide(FullStructure structure) {
        return structure.getLinks() != null && !structure.getLinks().isEmpty();
    }

    @Override
    public List<Website> computeField(FullStructure structure) {
        List<String> websiteIds = structure.getLinks().stream().map(Link::getId).filter(it -> it != null).collect(Collectors.toList());
        return ((List<Website>) repository.findAll(websiteIds)).stream().filter(it -> it.getPageCount() > 0).collect(Collectors.toList());
    }

    @Override
    public FullStructureField getField() {
        return FullStructureField.WEBSITES;
    }

    @Override
    public Set<FullStructureField> getDependencies() {
//        return FIELDS;
        return null;
    }
}
