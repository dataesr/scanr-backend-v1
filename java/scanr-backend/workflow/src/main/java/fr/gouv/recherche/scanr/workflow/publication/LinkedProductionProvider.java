package fr.gouv.recherche.scanr.workflow.publication;

import com.google.common.collect.Sets;
import fr.gouv.recherche.scanr.db.model.LinkedProduction;
import fr.gouv.recherche.scanr.db.model.full.FullPublication;
import fr.gouv.recherche.scanr.db.model.full.FullPublicationField;
import fr.gouv.recherche.scanr.db.model.publication.Publication;
import fr.gouv.recherche.scanr.db.model.publication.PublicationAuthorRelation;
import fr.gouv.recherche.scanr.db.repository.PublicationRepository;
import fr.gouv.recherche.scanr.workflow.full.FullPublicationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component("PublicationLinkedProductionProvider")
public class LinkedProductionProvider implements FullPublicationProvider<List<LinkedProduction>> {
    public static final Set<FullPublicationField> FIELDS = Sets.newHashSet();

    @Autowired
    private PublicationRepository repository;

    @Override
    public boolean canProvide(FullPublication publication) {
        return publication.getLinkedProductions() != null && !publication.getLinkedProductions().isEmpty();
    }

    @Override
    public List<LinkedProduction> computeField(FullPublication publication) {
            publication.getLinkedProductions().stream().filter(Objects::nonNull).forEach(linkedProduction -> {
                Publication production = repository.findByIdLight(linkedProduction.getProduction().getId());
                linkedProduction.setProduction(production);
            });

        return publication.getLinkedProductions();
    }

    @Override
    public FullPublicationField getField() {
        return FullPublicationField.LINKED_PRODUCTIONS;
    }

    @Override
    public Set<FullPublicationField> getDependencies() {
        return FIELDS;
    }
}
