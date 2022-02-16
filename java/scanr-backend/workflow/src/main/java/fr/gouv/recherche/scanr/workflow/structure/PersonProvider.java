/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.workflow.structure;

import com.google.common.collect.Sets;
import fr.gouv.recherche.scanr.db.model.Person;
import fr.gouv.recherche.scanr.db.model.PublicationPersonAffiliation;
import fr.gouv.recherche.scanr.db.model.full.FullStructure;
import fr.gouv.recherche.scanr.db.model.full.FullStructureField;
import fr.gouv.recherche.scanr.db.model.full.StructurePersonInverseRelation;
import fr.gouv.recherche.scanr.db.repository.PersonRepository;
import fr.gouv.recherche.scanr.workflow.full.FullStructureProvider;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Component("StructurePersonProvider")
public class PersonProvider implements FullStructureProvider<List<StructurePersonInverseRelation>> {
    public static final Set<FullStructureField> FIELDS = Sets.newHashSet();

    @Autowired
    private PersonRepository repository;

    @Override
    public boolean canProvide(FullStructure structure) {
        return structure != null && structure.getId() != null;
    }

    @Override
    public List<StructurePersonInverseRelation> computeField(FullStructure structure) {

        List<StructurePersonInverseRelation> personsRelations = structure.getPersons();
        personsRelations = personsRelations == null ? new ArrayList<StructurePersonInverseRelation>() : personsRelations;
        List<Person> persons = repository.findPersonByStructureIdInAffiliation(structure.getId());

        // Put the light version of the Person
        List<StructurePersonInverseRelation> finalPersonsRelations = personsRelations;
        persons.stream().filter(Objects::nonNull).forEach(person -> {
            // Search our structure in affiliations
            for (PublicationPersonAffiliation structureRelation : person.getAffiliations()) {
                if (!Objects.isNull(structureRelation.getStructure()) && StringUtils.equals(structureRelation.getStructure().getId(), structure.getId())) {
                    // Create the inverse relation
                    StructurePersonInverseRelation personRelation = new StructurePersonInverseRelation();
                    personRelation.setPerson(person.getUltraLightPerson());
                    personRelation.setRole(structureRelation.getRole());
                    personRelation.setStartDate(structureRelation.getStartDate());
                    personRelation.setEndDate(structureRelation.getEndDate());
                    personRelation.setSource(structureRelation.getSources());

                    finalPersonsRelations.add(personRelation);
                    break;
                }
            }
        });

        return personsRelations;
    }

    @Override
    public FullStructureField getField() {
        return FullStructureField.PERSONS;
    }

    @Override
    public Set<FullStructureField> getDependencies() {
        return FIELDS;
    }
}