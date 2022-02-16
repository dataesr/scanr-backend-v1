/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.db.model.publication;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import fr.gouv.recherche.scanr.db.model.Person;
import fr.gouv.recherche.scanr.db.model.PublicationStructureAffiliation;
import fr.gouv.recherche.scanr.db.model.RolePatent;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * 
 */
@ApiModel(value="v2.PublicationAuthor", description="Author relation of a publication")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class PublicationAuthorRelation {
	
    @ApiModelProperty("Person reference : Light fields in FullPublication, else only id")
    private Person person;
    
    // relation fields :
    private String role;
    
	// values for the Person in this Publication :
    /**
     * first name of the author
     */
    private String firstName;
    /**
     * last name of the author
     */
    private String lastName;
    private String fullName;
    // NB. email removed

    private List<RolePatent> rolePatent;
    private String typeParticipant;
    private String postcode;
    private String country;
    private String city;
    private String gender;

    private List<PublicationStructureAffiliation> affiliations;

    public PublicationAuthorRelation(String firstName, String lastName, List<PublicationStructureAffiliation> affiliations) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.affiliations = affiliations;
    }

    public PublicationAuthorRelation() {
    }

    /**
     * Get "light" version of current PublicationAuthorRelation
     * @return PublicationAuthorRelation
     */
    @JsonIgnore
    public PublicationAuthorRelation getLightPublicationAuthorRelation() {
        PublicationAuthorRelation publicationAuthorRelation = new PublicationAuthorRelation();

        if (this.getFullName() != null) {
            publicationAuthorRelation.setPerson(new Person());
            publicationAuthorRelation.getPerson().setFullName(this.getFullName());

            return publicationAuthorRelation;
        }

        if (this.getPerson() != null && this.getPerson().getFullName() != null) {
            publicationAuthorRelation.setPerson(this.getPerson().getLightPerson());
        }
        publicationAuthorRelation.setFirstName(this.firstName);
        publicationAuthorRelation.setLastName(this.lastName);

        publicationAuthorRelation.setRolePatent(this.rolePatent);
        publicationAuthorRelation.setTypeParticipant(this.typeParticipant);
        publicationAuthorRelation.setPostcode(this.postcode);
        publicationAuthorRelation.setCountry(this.country);
        publicationAuthorRelation.setCity(this.city);
        publicationAuthorRelation.setGender(this.gender);

        return publicationAuthorRelation;
    }

    public String getFirstName() {
        return firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public List<PublicationStructureAffiliation> getAffiliations() {
        return affiliations;
    }
    public void setAffiliations(List<PublicationStructureAffiliation> affiliations) {
        this.affiliations = affiliations;
    }
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public Person getPerson() {
		return person;
	}
	public void setPerson(Person person) {
		this.person = person;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

    public List<RolePatent> getRolePatent() {
        return rolePatent;
    }

    public void setRolePatent(List<RolePatent> rolePatent) {
        this.rolePatent = rolePatent;
    }

    public String getTypeParticipant() {
        return typeParticipant;
    }

    public void setTypeParticipant(String typeParticipant) {
        this.typeParticipant = typeParticipant;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
