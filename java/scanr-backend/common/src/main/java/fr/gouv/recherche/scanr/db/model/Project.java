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
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.*;

/**
 * Research project.
 * also FullProject (or in extending class ?)
 */
//@Document
@JsonInclude(JsonInclude.Include.NON_EMPTY)
//@CompoundIndexes({
//        @CompoundIndex(name = "participants.structure._id", def = "{\"participants.structure._id\":1}"),
//        @CompoundIndex(name = "persons.person._id", def = "{\"persons.person._id\":1}")
//})
@ApiModel("v2.Project")
public class Project implements IIdentifiable {

    // fields provided in Light version :

    @Id
    private String id;

    private String type;

    @ApiModelProperty("i18n since v2")
    private I18nValue label;
    @ApiModelProperty("i18n since v2")
    private I18nValue acronym;
    @ApiModelProperty("project's year")
    private Integer year;
    @ApiModelProperty("since v2")
    private Date startDate;
    @ApiModelProperty("since v2")
    private Date endDate;
    @ApiModelProperty("budget before v2")
    private Float budgetTotal;
    @ApiModelProperty("since v2")
    private Float budgetFinanced;
    @ApiModelProperty("in month")
    private Integer duration;
    @ApiModelProperty("project's url")
    private String url;
    @ApiModelProperty("since v2")
    private String projectUrl;
    private Integer participantCount;

    // Fields NOT provided in Light version :

    @ApiModelProperty("i18n since v2")
    private I18nValue description;

    @ApiModelProperty("project's participating structures (scanr structures or external structures). "
            + "In FullStructure Light fields, outside only id")
    private List<ProjectStructureRelation> participants;

    @ApiModelProperty("since v2")
    private Date signatureDate;

    private List<Domain> domains = new ArrayList<>(); // TODO renamed from themes (project's themes, list of free text) ? or focus ???

    /**
     * embedded before v2
     */
    private ProjectCall call;
    @ApiModelProperty("since v2")
    private ProjectAction action;
    @ApiModelProperty("since v2. Only id, firstName, lastName, email, plus Light fields in FullProject")
    private List<PersonRelation> persons = new ArrayList<>();
    private List<SimilarTypedObject<Project>> similarProjects = new ArrayList<>();
    @ApiModelProperty("in FullProject Light fields, outside only id")
    private List<Project> associatedProjects = new ArrayList<>();

    // TODO in extended generic class ?
    @ApiModelProperty("since v2")
    private List<String> focus = new ArrayList<>();
    private List<Badge> badges = new ArrayList<>();
    @ApiModelProperty("une liste de termes par langue (fr, en), fourni par MESRI")
    // Rend l'API keywords() caduque, dans tous les types
    private Map<String, List<String>> keywords;

    @CreatedDate
    private Date createdDate;
    @LastModifiedDate
    private Date lastUpdated;

    private Date createdAt;
    private Date removedAt;

    public Project() {
    }

    public Project(String id) {
        this.id = id;
    }

    public Project(String id, String type, I18nValue acronym, I18nValue label) {
        this.id = id;
        this.type = type;
        this.acronym = acronym;
        this.label = label;
    }

    /**
     * Get "light" version of current Project
     *
     * @return Project
     */
    @JsonIgnore
    public Project getLightProject() {
        Project lightProject = new Project();
        lightProject.setId(this.id);
        lightProject.setType(this.type);
        lightProject.setLabel(this.label);
        lightProject.setAcronym(this.acronym);
        lightProject.setYear(this.year);
        lightProject.setStartDate(this.startDate);
        lightProject.setEndDate(this.endDate);
        lightProject.setBudgetTotal(this.budgetTotal);
        lightProject.setBudgetFinanced(this.budgetFinanced);
        lightProject.setDuration(this.duration);
        lightProject.setUrl(this.url);
        lightProject.setProjectUrl(this.projectUrl);
        lightProject.setParticipantCount(this.participantCount);
        lightProject.setLastUpdated(this.lastUpdated);

        return lightProject;
    }

    /**
     * Get "ultra light" version of current Project
     *
     * @return Project
     */
    @JsonIgnore
    public Project getUltraLightProject() {
        Project lightProject = new Project();
        lightProject.setId(this.id);
        lightProject.setLabel(this.label);
        lightProject.setAcronym(this.acronym);
        lightProject.setType(this.type);

        return lightProject;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public I18nValue getLabel() {
        return label;
    }

    public void setLabel(I18nValue label) {
        this.label = label;
    }

    public I18nValue getDescription() {
        return description;
    }

    public void setDescription(I18nValue description) {
        this.description = description;
    }

    public I18nValue getAcronym() {
        return acronym;
    }

    public void setAcronym(I18nValue acronym) {
        this.acronym = acronym;
    }

    public List<ProjectStructureRelation> getParticipants() {
        return participants;
    }

    public void setParticipants(List<ProjectStructureRelation> participants) {
        this.participants = participants;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
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

    public Date getSignatureDate() {
        return signatureDate;
    }

    public void setSignatureDate(Date signatureDate) {
        this.signatureDate = signatureDate;
    }

    public Float getBudgetTotal() {
        return budgetTotal;
    }

    public void setBudgetTotal(Float budgetTotal) {
        this.budgetTotal = budgetTotal;
    }

    public Float getBudgetFinanced() {
        return budgetFinanced;
    }

    public void setBudgetFinanced(Float budgetFinanced) {
        this.budgetFinanced = budgetFinanced;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getProjectUrl() {
        return projectUrl;
    }

    public void setProjectUrl(String projectUrl) {
        this.projectUrl = projectUrl;
    }

    public Integer getParticipantCount() {
        return participantCount;
    }

    public void setParticipantCount(Integer participantCount) {
        this.participantCount = participantCount;
    }

    public List<Domain> getDomains() {
        return domains;
    }

    public void setDomains(List<Domain> domains) {
        this.domains = domains;
    }

    public ProjectCall getCall() {
        return call;
    }

    public void setCall(ProjectCall call) {
        this.call = call;
    }

    public ProjectAction getAction() {
        return action;
    }

    public void setAction(ProjectAction action) {
        this.action = action;
    }

    public List<PersonRelation> getPersons() {
        return persons;
    }

    public void setPersons(List<PersonRelation> persons) {
        this.persons = persons;
    }

    public List<SimilarTypedObject<Project>> getSimilarProjects() {
        return similarProjects;
    }

    public void setSimilarProjects(List<SimilarTypedObject<Project>> similarProjects) {
        this.similarProjects = similarProjects;
    }

    public List<Project> getAssociatedProjects() {
        return associatedProjects;
    }

    public void setAssociatedProjects(List<Project> associatedProjects) {
        this.associatedProjects = associatedProjects;
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

}
