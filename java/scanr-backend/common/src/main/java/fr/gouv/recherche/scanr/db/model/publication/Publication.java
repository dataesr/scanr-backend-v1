/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.db.model.publication;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.sword.utils.elasticsearch.intf.IIdentifiable;
import fr.gouv.recherche.scanr.db.model.*;
import fr.gouv.recherche.scanr.util.TolerantDateDeserializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import me.xuender.unidecode.Unidecode;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;

import java.util.*;

/**
 * Publication in scanr
 */
@ApiModel("v2.Publication")
//@Document
@JsonInclude(JsonInclude.Include.NON_EMPTY)
//@CompoundIndexes({
//		@CompoundIndex(name = "affiliations._id", def = "{\"affiliations._id\":1}"),
//		@CompoundIndex(name = "authors.person._id", def = "{\"authors.person._id\":1}"),
//		@CompoundIndex(name = "projects._id", def = "{\"projects._id\":1}")
//})
public class Publication implements IIdentifiable {

	// fields provided in Light version :
	
    @Id
    private String id;
    @ApiModelProperty("Publication Type")
    private String type;
	private String productionType;
    @ApiModelProperty("since v2 i18n")
    private I18nValue title;
    private List<PublicationAuthorRelation> authors = new ArrayList<>();
    private Integer authorsCount;
    private List<ExternalId> externalIds = new ArrayList<>();
    @ApiModelProperty("is in Open Access publishing")
    private boolean isOa;
    @ApiModelProperty("Known publication date (may be null)")
    @JsonDeserialize(using = TolerantDateDeserializer.class)
    private Date publicationDate;
    private Integer year;
    @ApiModelProperty("in FullPublication Light fields and only the first 10 publications, outside only id")
    private List<Publication> citedBy = new ArrayList<>();
    @ApiModelProperty("Count of all members of the citedBy relationship")
    private Integer citedByCount;

	// Fields NOT provided in Light version :

	@ApiModelProperty("Article processing charge, amount of currency")
    private Float apc;
    private String doiUrl;
    @ApiModelProperty("since v2 i18n")
    private I18nValue subtitle;
    @ApiModelProperty("since v2 i18n")
    private I18nValue summary;
    @ApiModelProperty("alternative summary of the publication (often the english summary), since v2 i18n")
    private I18nValue alternativeSummary;
    private Boolean isFrench; // not true by default
    @ApiModelProperty(example="fr")
    private String language;
    private List<Project> projects = new ArrayList<>();
    @ApiModelProperty("Where the publication has been published (journal, event...)")
    private PublicationSource source;
    private PublicationOaEvidence oaEvidence;
    
    private List<Domain> domains = new ArrayList<>();

    private Date submissionDate;
    private Date grantedDate;
    @ApiModelProperty("une liste de termes par langue (fr, en), fourni par MESRI") // Rend l'API keywords() caduque, dans tous les types
    private Map<String,List<String>> keywords;
    @ApiModelProperty("in FullPublication Light fields, outside only id")
    private List<Publication> citations = new ArrayList<>();
    private List<String> references = new ArrayList<>();
    private Integer referencesCount;
    @ApiModelProperty("collection of link type (twitter, wikipedia) to url")
    private List<Link> links = new ArrayList<>();
    private List<Certification> certifications = new ArrayList<>();
    private List<Award> awards = new ArrayList<>();
    private List<SimilarTypedObject<Publication>> similarPublications = new ArrayList<>();
    private List<Structure> affiliations = new ArrayList<>();

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

	private String inventionKind;
	private String inpadocFamily;
	private Boolean isInternational;
	private Boolean isOeb;

	private List<Patent> patents;
	private List<LinkedProduction> linkedProductions;

    public Publication(I18nValue title, String type, PublicationAuthorRelation firstAuthor) {
        this.title = title;
        this.type = type;
        authors.add(firstAuthor);
        this.id = computeId();
    }

	public Publication(String id) {
    	this.id = id;
	}

    public Publication() {
    }


    public String computeId() {
        // safety check
        if (title == null || title.get(I18nValue.DEFAULT_LANGUAGE) == null) return null;

        // for Patents, use the patent Id
        String normalizedTitle = Unidecode.decode(title.get(I18nValue.DEFAULT_LANGUAGE)).toLowerCase().replaceAll("[^a-z0-9]*", "");
        String result;

		if (title == null || authors.isEmpty() || authors.get(0).getLastName() == null) {
			return null;
		}
		String author = Unidecode.decode(authors.get(0).getLastName()).toLowerCase().replaceAll("[^a-z0-9]*", "");
		result = author + ":" + normalizedTitle;

        // Ignore long title (only few cases mostly due to wrong formatting rules on the provider side)
        if (result.length() > 1000) {
            return null;
        }
        return result;
    }

	/**
	 * Get "light" version of current Publication
	 * @return Publication
	 */
	@JsonIgnore
	public Publication getLightPublication() {
		Publication lightPublication = new Publication();
		lightPublication.setId(this.id);
		lightPublication.setType(this.type);
		lightPublication.setProductionType(this.productionType);
		lightPublication.setTitle(this.title);

		if (this.authors != null) {
			List<PublicationAuthorRelation> lightAuthors = new ArrayList<>();
			for (PublicationAuthorRelation authorRelation : this.authors) {
				PublicationAuthorRelation publicationAuthorRelation = authorRelation.getLightPublicationAuthorRelation();
				if (publicationAuthorRelation.getPerson() != null && publicationAuthorRelation.getPerson().getFullName() != null) {
					lightAuthors.add(publicationAuthorRelation);
				}
			}
			lightPublication.setAuthors(lightAuthors);
		}

		lightPublication.setAuthorsCount(this.authorsCount);
		lightPublication.setExternalIds(this.externalIds);
		lightPublication.setIsOa(this.isOa);
		lightPublication.setPublicationDate(this.publicationDate);
		lightPublication.setLastUpdated(this.lastUpdated);
		lightPublication.setInventionKind(this.inventionKind);
		lightPublication.setInpadocFamily(this.inpadocFamily);
		lightPublication.setIsInternational(this.isInternational);
		lightPublication.setIsOeb(this.isOeb);
		lightPublication.setSubmissionDate(this.submissionDate);
		lightPublication.setGrantedDate(this.grantedDate);
		lightPublication.setAuthors(this.authors);
		// Max 10 citedBy in light
		if(this.citedBy == null || this.citedBy.size() < 10) {
			lightPublication.setCitedBy(this.citedBy);
		}
		else {
			lightPublication.setCitedBy(this.citedBy.subList(0,10));
		}
		lightPublication.setKeywords(this.keywords);

		return lightPublication;
	}



	/**
	 * Get "ultra light" version of current Publication
	 * @return Publication
	 */
	@JsonIgnore
	public Publication getUltraLightPublication() {
		Publication lightPublication = new Publication();
		lightPublication.setId(this.id);
		lightPublication.setTitle(this.title);
		lightPublication.setKeywords(this.keywords);

		return lightPublication;
	}

    @Override
    public int hashCode() {
        return Objects.hash(id, lastUpdated);
    }

	public List<Structure> getAffiliations() {
		return affiliations;
	}

	public void setAffiliations(List<Structure> affiliations) {
		this.affiliations = affiliations;
	}

	public String getProductionType() {
		return productionType;
	}

	public void setProductionType(String productionType) {
		this.productionType = productionType;
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public I18nValue getTitle() {
		return title;
	}

	public void setTitle(I18nValue title) {
		this.title = title;
	}

	public List<PublicationAuthorRelation> getAuthors() {
		return authors;
	}

	public void setAuthors(List<PublicationAuthorRelation> authors) {
		this.authors = authors;
	}

	public Integer getAuthorsCount() {
		return authorsCount;
	}

	public void setAuthorsCount(Integer authorsCount) {
		this.authorsCount = authorsCount;
	}

	public List<ExternalId> getExternalIds() {
		return externalIds;
	}

	public void setExternalIds(List<ExternalId> externalIds) {
		this.externalIds = externalIds;
	}

	public boolean getIsOa() {
		return isOa;
	}

	public void setIsOa(boolean oa) {
		this.isOa = oa;
	}

	public Date getPublicationDate() {
		return publicationDate;
	}

	public void setPublicationDate(Date publicationDate) {
		this.publicationDate = publicationDate;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public Float getApc() {
		return apc;
	}

	public void setApc(Float apc) {
		this.apc = apc;
	}

	public String getDoiUrl() {
		return doiUrl;
	}

	public void setDoiUrl(String doiUrl) {
		this.doiUrl = doiUrl;
	}

	public I18nValue getSubtitle() {
		return subtitle;
	}

	public void setSubtitle(I18nValue subtitle) {
		this.subtitle = subtitle;
	}

	public I18nValue getSummary() {
		return summary;
	}

	public void setSummary(I18nValue summary) {
		this.summary = summary;
	}

	public I18nValue getAlternativeSummary() {
		return alternativeSummary;
	}

	public void setAlternativeSummary(I18nValue alternativeSummary) {
		this.alternativeSummary = alternativeSummary;
	}

	public Boolean getIsFrench() {
		return isFrench;
	}

	public void setIsFrench(Boolean isFrench) {
		this.isFrench = isFrench;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public List<Project> getProjects() {
		return projects;
	}

	public void setProjects(List<Project> projects) {
		this.projects = projects;
	}

	public PublicationSource getSource() {
		return source;
	}

	public void setSource(PublicationSource source) {
		this.source = source;
	}

	public PublicationOaEvidence getOaEvidence() {
		return oaEvidence;
	}

	public void setOaEvidence(PublicationOaEvidence oaEvidence) {
		this.oaEvidence = oaEvidence;
	}

	public List<Domain> getDomains() {
		return domains;
	}

	public void setDomains(List<Domain> domains) {
		this.domains = domains;
	}

	public Date getSubmissionDate() {
		return submissionDate;
	}

	public void setSubmissionDate(Date submissionDate) {
		this.submissionDate = submissionDate;
	}

	public Date getGrantedDate() {
		return grantedDate;
	}

	public void setGrantedDate(Date grantedDate) {
		this.grantedDate = grantedDate;
	}

	public Map<String,List<String>> getKeywords() {
		return keywords;
	}

	public void setKeywords(Map<String,List<String>> keywords) {
		this.keywords = keywords;
	}

	public List<Publication> getCitations() {
		return citations;
	}

	public void setCitations(List<Publication> citations) {
		this.citations = citations;
	}

	public List<Publication> getCitedBy() {
		return citedBy;
	}

	public void setCitedBy(List<Publication> citedBy) {
		this.citedBy = citedBy;
	}

	public Integer getCitedByCount() {
		return citedByCount;
	}

	public void setCitedByCount(Integer citedByCount) {
		this.citedByCount = citedByCount;
	}

	public List<String> getReferences() {
		return references;
	}

	public void setReferences(List<String> references) {
		this.references = references;
	}

	public Integer getReferencesCount() {
		return referencesCount;
	}

	public void setReferencesCount(Integer referencesCount) {
		this.referencesCount = referencesCount;
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

	public List<SimilarTypedObject<Publication>> getSimilarPublications() {
		return similarPublications;
	}

	public void setSimilarPublications(List<SimilarTypedObject<Publication>> similarPublications) {
		this.similarPublications = similarPublications;
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

	public String getInventionKind() {
		return inventionKind;
	}

	public void setInventionKind(String inventionKind) {
		this.inventionKind = inventionKind;
	}

	public String getInpadocFamily() {
		return inpadocFamily;
	}

	public void setInpadocFamily(String inpadocFamily) {
		this.inpadocFamily = inpadocFamily;
	}

	public Boolean getIsInternational() {
		return isInternational;
	}

	public void setIsInternational(Boolean international) {
		isInternational = international;
	}

	public Boolean getIsOeb() {
		return isOeb;
	}

	public void setIsOeb(Boolean oeb) {
		isOeb = oeb;
	}

	public List<Patent> getPatents() {
		return patents;
	}

	public void setPatents(List<Patent> patents) {
		this.patents = patents;
	}

	public List<LinkedProduction> getLinkedProductions() {
		return linkedProductions;
	}

	public void setLinkedProductions(List<LinkedProduction> linkedProductions) {
		this.linkedProductions = linkedProductions;
	}

}
