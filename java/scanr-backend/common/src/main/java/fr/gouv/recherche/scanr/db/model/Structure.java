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

import java.util.*;
import java.util.stream.Collectors;

//import org.springframework.data.mongodb.core.mapping.Document;
//import fr.gouv.recherche.scanr.search.model2.request.SearchFilter;

/**
 * Une structure est le concept abstrait permettant de représenter une structure de recherche publique
 * ou une entreprise privée et publique qui sera présente dans ScanR.
 */
//@Document
@JsonInclude(JsonInclude.Include.NON_EMPTY)
//@CompoundIndexes({
//        @CompoundIndex(name = "institutions.code.normalized", def = "{\"institutions.code.normalized\":1}"),
//        @CompoundIndex(name = "leaders.person._id", def = "{\"leaders.person._id\":1}"),
//        @CompoundIndex(name = "parents.structure._id", def = "{\"parents.structure._id\":1}"),
//        @CompoundIndex(name = "relations._id", def = "{\"relations._id\":1}"),
//        @CompoundIndex(name = "spinoffs.project._id", def = "{\"spinoffs.project._id\":1}"),
//        @CompoundIndex(name = "spinoffs.structure._id", def = "{\"spinoffs.structure._id\":1}")
//})
// BEWARE probably uselesss because tags had already been removed (deprecated and no @Query on it)
@ApiModel("v2.Structure")
public class Structure implements IIdentifiable {

    // fields provided in Light version :

    @ApiModelProperty("RNSR ID for research structures (UAI ex. 0755361V), SIREN for public and private companies (ex. 18007003901803)")
    @Id
    private String id;
    @ApiModelProperty(value = "Structure Kind, not Enum since v2")
    private List<String> kind;
    // NB. Swagger lists Enum values ; otherwise customize it with Arrays.stream(StructureKind.values()).map(Object::toString).collect(Collectors.joining(", "))
    @ApiModelProperty("logo URL, can be null")
    private String logo;
    @ApiModelProperty(value = "Full label of the structure. I18n since v2 (e.g. Laboratoire d'Informatique de Grenoble)")
    private I18nValue label;
    @ApiModelProperty("Acronym of the structure. I18n since v2")
    private I18nValue acronym;
    @ApiModelProperty(value = "in v2 merges nature and companyType", example = "5400 / Société à responsabilité limitée")
    private String nature;
    @ApiModelProperty(value = "si la Structure est toujours existante ou non (Active, Old), since v2")
    private String status;
    @ApiModelProperty("Whether this is a French structure. Since v2")
    private Boolean isFrench; // and not foreign ; computed by MESRI
    @ApiModelProperty("In Light only main one, since v2 multivalued")
    private List<Address> address = new ArrayList<Address>();

    // TODO fields NOT provided in Light version :

    @ApiModelProperty("Alternative commercial label for companies. Before v2 commercialLabel and single valued")
    private List<String> commercialBrands; // TODO Renommé (et multivalué) de commercialLabel

    @ApiModelProperty("Alternative labels for search (other names for research structures...)")
    private List<String> alias;
    @ApiModelProperty("since v2")
    private I18nValue description;

    @ApiModelProperty("Whether this is a public structure. Since v2")
    private Boolean isPublic; // in addition to kind, computed by MESRI

    @ApiModelProperty("in v2 renamed from parent")
    private List<StructureParentRelation> parents;

    @ApiModelProperty("History of the research structure (merge and name changes). In v2 renamed from history and multivalued")
    private List<StructurePredecessorRelation> predecessors; // dans Full, renommé de parent
    @ApiModelProperty("Since v2")
    private LegalCategory legalCategory;

    /**
     * Structure type
     * (e.g. for public structures: Unité de recherche mixte, Centre de Recherche; for private structures: PME, Grand Groupes)
     */
    //private StructureType type; // TODO remove ?!

    /**
     * (Private only) Detailed structure types for companies ()
     */
    //private CompanyType companyType; // TODO remove ?!

    @ApiModelProperty("public structure level label, before v2 was integer")
    private String level;
    private Integer creationYear;
    private List<Link> links;
    @ApiModelProperty("(Public only) Related institutions aka Tutelles")
    private List<InstitutionRelation> institutions; // kept
    @ApiModelProperty("List of all leading people of the structure")
    private List<PersonRelation> leaders;
    @ApiModelProperty("All foreign structural relations with this structure (e.g. COMUE, École Doctorales...)")
    private List<StructureRelation> relations;
    @ApiModelProperty("All activity labels that are affected to this structure (with or without a nomenclature)")
    private List<StructureActivity> activities;
    @ApiModelProperty("Domain of research")
    private List<Domain> domains;
    private StructureEmployeesInfo employeesInfo;
    private StructureFinance finance; // before v2 separated in privateFinance and (public) finance
    @ApiModelProperty("List of badges (HDR. ILab...). Badges are associated with symbols and displayed in the front end. Before v2 was tags.")
    private List<Badge> badges;
    @ApiModelProperty("List of spinoff of this RNSR lab.")
    private List<StructureSpinoff> spinoffs; // NB. not the inverse relation, because there are not that many
    @ApiModelProperty("Date de début de la structure, since v2")
    private Date startDate;
    @ApiModelProperty("Date de fin de la structure, since v2")
    private Date endDate;
    @ApiModelProperty("Mail de la structure, since v2")
    private String email;
    @ApiModelProperty("Téléphone de la structure, since v2")
    private String phone;
    @ApiModelProperty("type SocialMedia, since v2. Differs from crawled Website.SocialAccount")
    private List<SocialMedia> socialMedias;
    @ApiModelProperty("collection of idName : idValue")
    private List<ExternalId> externalIds = new ArrayList<>();
    private List<StructureEvaluation> evaluations;
    private List<StructureOffer> offers;
    @ApiModelProperty("since v2")
    private List<String> focus;
    @ApiModelProperty("une liste de termes par langue (fr, en), fourni par MESRI")
    // Rend l'API keywords() caduque, dans tous les types
    private Map<String, List<String>> keywords;

    @CreatedDate
    private Date createdDate;
    @LastModifiedDate
    private Date lastUpdated;

    private Date createdAt;
    private Date removedAt;

    public Structure() {
    }

    public Structure(String id) {
        this.id = id;
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

    public List<String> getKind() {
        return kind;
    }

    public void setKind(List<String> kind) {
        this.kind = kind;
    }

    public I18nValue getLabel() {
        return label;
    }

    public void setLabel(I18nValue label) {
        this.label = label;
    }

    public List<String> getCommercialBrands() {
        return commercialBrands;
    }

    public void setCommercialBrands(List<String> commercialBrands) {
        this.commercialBrands = commercialBrands;
    }

    public List<String> getAlias() {
        return alias;
    }

    public void setAlias(List<String> alias) {
        this.alias = alias;
    }

    public I18nValue getDescription() {
        return description;
    }

    public void setDescription(I18nValue description) {
        this.description = description;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public I18nValue getAcronym() {
        return acronym;
    }

    public void setAcronym(I18nValue acronym) {
        this.acronym = acronym;
    }

    public Boolean getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }

    public Boolean getIsFrench() {
        return isFrench;
    }

    public void setIsFrench(Boolean isFrench) {
        this.isFrench = isFrench;
    }

    public List<StructureParentRelation> getParents() {
        return parents;
    }

    public void setParents(List<StructureParentRelation> parents) {
        this.parents = parents;
    }

    public List<StructurePredecessorRelation> getPredecessors() {
        return predecessors;
    }

    public void setPredecessors(List<StructurePredecessorRelation> predecessors) {
        this.predecessors = predecessors;
    }

    public LegalCategory getLegalCategory() {
        return legalCategory;
    }

    public void setLegalCategory(LegalCategory legalCategory) {
        this.legalCategory = legalCategory;
    }

    public String getNature() {
        return nature;
    }

    public void setNature(String nature) {
        this.nature = nature;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public List<Address> getAddress() {
        return address;
    }

    public void setAddress(List<Address> address) {
        this.address = address;
    }

    public Integer getCreationYear() {
        return creationYear;
    }

    public void setCreationYear(Integer creationYear) {
        this.creationYear = creationYear;
    }

    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }

    public List<InstitutionRelation> getInstitutions() {
        return institutions;
    }

    public void setInstitutions(List<InstitutionRelation> institutions) {
        this.institutions = institutions;
    }

    public List<PersonRelation> getLeaders() {
        return leaders;
    }

    public void setLeaders(List<PersonRelation> leaders) {
        this.leaders = leaders;
    }

    public List<StructureRelation> getRelations() {
        return relations;
    }

    public void setRelations(List<StructureRelation> relations) {
        this.relations = relations;
    }

    public List<StructureActivity> getActivities() {
        return activities;
    }

    public void setActivities(List<StructureActivity> activities) {
        this.activities = activities;
    }

    public List<Domain> getDomains() {
        return domains;
    }

    public void setDomains(List<Domain> domains) {
        this.domains = domains;
    }

    public StructureEmployeesInfo getEmployeesInfo() {
        return employeesInfo;
    }

    public void setEmployeesInfo(StructureEmployeesInfo employeesInfo) {
        this.employeesInfo = employeesInfo;
    }

    public StructureFinance getFinance() {
        return finance;
    }

    public void setFinance(StructureFinance finance) {
        this.finance = finance;
    }

    public List<Badge> getBadges() {
        return badges;
    }

    public void setBadges(List<Badge> badges) {
        this.badges = badges;
    }

    public List<StructureSpinoff> getSpinoffs() {
        return spinoffs;
    }

    public void setSpinoffs(List<StructureSpinoff> spinoffs) {
        this.spinoffs = spinoffs;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public List<SocialMedia> getSocialMedias() {
        return socialMedias;
    }

    public void setSocialMedias(List<SocialMedia> socialMedias) {
        this.socialMedias = socialMedias;
    }

    public List<ExternalId> getExternalIds() {
        return externalIds;
    }

    public void setExternalIds(List<ExternalId> externalIds) {
        this.externalIds = externalIds;
    }

    public List<StructureEvaluation> getEvaluations() {
        return evaluations;
    }

    public void setEvaluations(List<StructureEvaluation> evaluations) {
        this.evaluations = evaluations;
    }

    public List<StructureOffer> getOffers() {
        return offers;
    }

    public void setOffers(List<StructureOffer> offers) {
        this.offers = offers;
    }

    public List<String> getFocus() {
        return focus;
    }

    public void setFocus(List<String> focus) {
        this.focus = focus;
    }

    public Map<String, List<String>> getKeywords() {
        return keywords;
    }

    public void setKeywords(Map<String, List<String>> keywords) {
        this.keywords = keywords;
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

    /**
     * Get "light" version of current Structure
     *
     * @return Structure
     */
    @JsonIgnore
    public Structure getLightStructure() {
        Structure lightStructure = new Structure();
        lightStructure.setId(this.id);
        lightStructure.setKind(this.kind);
        lightStructure.setLogo(this.logo);
        lightStructure.setLabel(this.label);
        lightStructure.setAcronym(this.acronym);
        lightStructure.setNature(this.nature);
        lightStructure.setStatus(this.status);
        lightStructure.setIsFrench(this.isFrench);
        lightStructure.setLastUpdated(this.lastUpdated);

        if (this.getAddress() != null) {
            lightStructure.setAddress(getMainAddressList());
        }

        return lightStructure;
    }

    /**
     * Return the list of Address items marked as main
     *
     * @return List<Address>
     */
    @JsonIgnore
    public List<Address> getMainAddressList() {
        List<Address> addresses = new ArrayList<>();

        if (this.getAddress() != null) {
            return this.getAddress().stream().filter(Address::isMain).collect(Collectors.toList());
        }
        return addresses;
    }
}
