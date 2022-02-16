/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.db.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.sword.utils.elasticsearch.intf.IIdentifiable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.*;

/**
 * Une personne
 */
//@Document
@JsonInclude(JsonInclude.Include.NON_EMPTY)
//@CompoundIndexes({
//		@CompoundIndex(name = "affiliations.structure._id", def = "{\"affiliations.structure._id\":1}"),
//		@CompoundIndex(name = "awards.structure._id", def = "{\"awards.structure._id\":1}")
//})
@ApiModel("v2.Person")
public class Person implements IIdentifiable {

	// fields provided in Light version :
	
    @Id
    private String id;

    private String firstName;
    private String lastName;
    private String maidenName;
    private String fullName;
    private String gender;
    @ApiModelProperty("roles génériques, pas de lien avec employeurs, fourni")
    private List<PersonRole> roles = new ArrayList<>();
    @ApiModelProperty("diffère de FullPerson.publications.affiliations")
    private List<PublicationPersonAffiliation> affiliations = new ArrayList<>();
    @ApiModelProperty("Map of external id name (idref, orcid, idhal ?) to value")
    private List<ExternalId> externalIds = new ArrayList<>();
    private List<Award> awards = new ArrayList<>(); // rather than prizes

	// Fields NOT provided in Light version :

    private List<String> dataSources;
    @ApiModelProperty("Light fields in Full objects, else only id of Persons that are Publication.coContributors.")
    private List<Person> coContributors;
    private Date orcidCreationDate;
    private I18nValue description;
    private List<Domain> domains = new ArrayList<>();
    private Date deathDate;
    private Date birthDate;
    @ApiModelProperty("une liste de termes par langue (fr, en), fourni par MESRI") // Rend l'API keywords() caduque, dans tous les types
    private Map<String,List<String>> keywords;
    private String email;
    private String website;
    @ApiModelProperty("collection of linkType : url. Including twitter, wikipedia")
    private List<Link> links = new ArrayList<>(); // should be List<Link> in order reuse the existing mechanism (crawl... MAIS NE SERA PAS FAIT DONC FOURNIRONT TEXTE)
    private List<Certification> certifications = new ArrayList<>();
    private List<SimilarTypedObject<Person>> similarPersons = new ArrayList<>();

    @ApiModelProperty("since v2")
    private List<String> focus;
    @ApiModelProperty("since v2")
    private List<Badge> badges;

    @CreatedDate
    private Date createdDate;
    @LastModifiedDate
    private Date lastUpdated;

	private Date createdAt;
	private Date removedAt;

    public Person() {
    }

    public Person(String id) {
        this.id = id;
    }

	/**
	 * Get "light" version of current Person
	 * @return Person
	 */
	@JsonIgnore
    public Person getLightPerson() {
    	Person person = new Person();
    	person.setId(this.id);
		person.setFirstName(this.firstName);
		person.setLastName(this.lastName);
		person.setMaidenName(this.maidenName);
		person.setFullName(this.fullName);
		person.setGender(this.gender);
		person.setRoles(this.roles);
		person.setAffiliations(this.affiliations);
		person.setExternalIds(this.externalIds);
		person.setAwards(this.awards);
		person.setLastUpdated(this.lastUpdated);

		return person;
	}

	/**
	 * Get "ultra light" version of current Person
	 * @return Person
	 */
	@JsonIgnore
	public Person getUltraLightPerson() {
		Person person = new Person();
		person.setId(this.id);
		person.setFullName(this.fullName);

		return person;
	}

    @Override
    public int hashCode() {
        return Objects.hash(id, lastUpdated);
    }

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Date getRemovedAt() {
		return removedAt;
	}

	public void setRemovedAt(Date removedAt) {
		this.removedAt = removedAt;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getMaidenName() {
		return maidenName;
	}

	public void setMaidenName(String maidenName) {
		this.maidenName = maidenName;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public List<String> getDataSources() {
		return dataSources;
	}

	public void setDataSources(List<String> dataSources) {
		this.dataSources = dataSources;
	}

	public List<Person> getCoContributors() {
		return coContributors;
	}

	public void setCoContributors(List<Person> coContributors) {
		this.coContributors = coContributors;
	}

	public Date getOrcidCreationDate() {
		return orcidCreationDate;
	}

	public void setOrcidCreationDate(Date orcidCreationDate) {
		this.orcidCreationDate = orcidCreationDate;
	}

	public I18nValue getDescription() {
		return description;
	}

	public void setDescription(I18nValue description) {
		this.description = description;
	}

	public List<PublicationPersonAffiliation> getAffiliations() {
		return affiliations;
	}

	public void setAffiliations(List<PublicationPersonAffiliation> affiliations) {
		this.affiliations = affiliations;
	}

	public List<PersonRole> getRoles() {
		return roles;
	}

	public void setRoles(List<PersonRole> roles) {
		this.roles = roles;
	}

	public List<Domain> getDomains() {
		return domains;
	}

	public void setDomains(List<Domain> domains) {
		this.domains = domains;
	}

	public List<ExternalId> getExternalIds() {
		return externalIds;
	}

	public void setExternalIds(List<ExternalId> externalIds) {
		this.externalIds = externalIds;
	}

	public Date getDeathDate() {
		return deathDate;
	}

	public void setDeathDate(Date deathDate) {
		this.deathDate = deathDate;
	}

	public Date getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	public Map<String,List<String>> getKeywords() {
		return keywords;
	}

	public void setKeywords(Map<String,List<String>> keywords) {
		this.keywords = keywords;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public List<Link> getLinks() {
		return links;
	}

	public void setLinks(List<Link> links) {
		this.links = links;
	}

	public List<Certification> getCertifications() {
		return certifications;
	}

	public void setCertifications(List<Certification> certifications) {
		this.certifications = certifications;
	}

	public List<Award> getAwards() {
		return awards;
	}

	public void setAwards(List<Award> awards) {
		this.awards = awards;
	}

	public List<SimilarTypedObject<Person>> getSimilarPersons() {
		return similarPersons;
	}

	public void setSimilarPersons(List<SimilarTypedObject<Person>> similarPersons) {
		this.similarPersons = similarPersons;
	}

	public List<String> getFocus() {
		return focus;
	}

	public void setFocus(List<String> focus) {
		this.focus = focus;
	}

	public List<Badge> getBadges() {
		return badges;
	}

	public void setBadges(List<Badge> badges) {
		this.badges = badges;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}
}
