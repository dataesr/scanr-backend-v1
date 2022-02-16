/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.db.model.full;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.collect.Sets;
import fr.gouv.recherche.scanr.config.elasticsearch.EsClient;
import fr.gouv.recherche.scanr.db.model.Structure;
import fr.gouv.recherche.scanr.db.model.Website;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;
import java.util.Set;

/**
 * Full structure: gather all the information (publication, projects...) of a Structure.
 * Not in Structure mainly to save it separately in MongoDB. 
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@ApiModel("v2.FullStructure")
public class FullStructure extends Structure {

	// Elasticsearch
	public static final String KEYWORD_SUFFIXE = EsClient.KEYWORD_SUFFIXE;

	// FIELDS
	public static final class FIELDS {
		public static final String ID = "id";
		public static final String ALIAS = "alias";
		public static final String ACRONYM = "acronym";
		public static final String KIND = "kind";
		public static final String KEYWORDS = "keywords";
		public static final String NATURE = "nature";
		public static final String LABEL = "label";
		public static final String LEVEL = "level";
//		public static final String BADGES = "badges";
		public static final String IS_FRENCH = "isFrench";
		public static final String STATUS = "status";
		public static final String STATUS_ACTIVE = "active";
		public static final String STATUS_OLD = "old";
		public static final String DESCRIPTION = "description";

		public static final class BADGES {
			public static final String TITLE = "badges";
			public static final String PREFIX = TITLE + ".";
			public static final String LABEL = PREFIX + "label";
			public static final String CODE = PREFIX + "code";
		}

		public static final class ACTIVITIES {
			public static final String TITLE = "activities";
			public static final String PREFIX = TITLE + ".";
			public static final String LABEL = PREFIX + "label";
		}

		public static final class EXTERNAL_IDS {
			public static final String TITLE = "externalIds";
			public static final String PREFIX = TITLE + ".";
			public static final String ID = PREFIX + "id";
		}

		public static final class INSTITUTIONS {
			public static final String TITLE = "institutions";
			public static final String STRUCTURE = "structure";
			public static final String PREFIX = TITLE + "." + STRUCTURE + ".";
			public static final String CODE = TITLE + ".code.normalized";
			public static final String LABEL = PREFIX + "label";
		}

		public static final class ADDRESS {
			public static final String TITLE = "address";
			public static final String PREFIX = TITLE + ".";
			public static final String POSTCODE = PREFIX + "postcode";
			public static final String URBAN_UNIT_CODE = PREFIX + "urbanUnitCode";
			public static final String URBAN_UNIT_LABEL = PREFIX + "urbanUnitLabel";
			public static final String DEPARTEMENT = PREFIX + "departement";
			public static final String CITY = PREFIX + "city";
			public static final String GPS = PREFIX + "gps";
			public static final String LOCALISATIONS = PREFIX + "localisationSuggestions";
		}

		public static final class LEADERS {
			public static final String TITLE = "leaders.person";
			public static final String PREFIX = TITLE + ".";
			public static final String FULLNAME = PREFIX + "fullName";
			public static final String ID = PREFIX + "id";
		}

		public static final class WEBSITES {
			public static final String TITLE = "websites";
			public static final String PREFIX = TITLE + ".";

			public static final class WEBPAGES {
				public static final String PREFIX = WEBSITES.PREFIX + "webPages";
				public static final String CONTENT = PREFIX + ".content";
			}
		}

		public static final class PUBLICATIONS {
			public static final String FIELDNAME = "publications";
			public static final String PREFIX = FIELDNAME + ".publication.";
			public static final String TITLE = PREFIX + "title";
			public static final String ID = PREFIX + "id";
			public static final String SUBTITLE = PREFIX + "subtitle";
			public static final String AUTHORS = PREFIX + "authors.person.fullName";
			public static final String SUMMARY = PREFIX + "summary";
			public static final String ALTERNATIVE_SUMMARY = PREFIX + "alternativeSummary";
		}

		public static final class PROJECTS {
			public static final String TITLE = "projects";
			public static final String PREFIX = TITLE + ".project.";
			public static final String ID = PREFIX + "id";
			public static final String ACRONYM = PREFIX + "acronym";
			public static final String LABEL = PREFIX + "label";
			public static final String DESCRIPTION = PREFIX + "description";
			public static final String CALL = PREFIX + "call";
			public static final String CALL_LABEL = PREFIX + "callLabel";
			public static final String TYPE = PREFIX + "type";
		}

		// Fields de langue
		public static final String LANG_FR = "fr";
		public static final String LANG_EN = "en";
	}

    @ApiModelProperty("LightStructure children of this structure, à partir de la relation inverse Structure.parents, avec métas de relation")
    private List<StructureChildInverseRelation> children;
    @ApiModelProperty("à partir de la relation inverse Person/affiliations/id, avec les métas de relation")
    private List<StructurePersonInverseRelation> persons;
    @ApiModelProperty("Since v2")
    private List<SpinoffFromInverseRelation> spinoffFrom;
    
    @ApiModelProperty("list of crawled websites informations for the websites of this structure.")
    private List<Website> websites;

    @ApiModelProperty("Inverse relation of LightProjects attached to the structure")
    private List<StructureProjectInverseRelation> projects;

    @ApiModelProperty("Inverse relation of LightPublications attached to the structure. NB. pas aussi LightPerson et LightStructure car LightPublication contient les 3 premiers authors")
    private List<StructurePublicationInverseRelation> publications;

    @ApiModelProperty("Graph Elements of this structure (relationships towards other structures)")
    private List<GraphElement> graph;

	@JsonIgnore
    private Set<FullStructureField> fieldsToRefresh = Sets.newHashSet();
    
 	public FullStructure() {
	}

	public FullStructure(String id) {
		this.setId(id);
	}

	public Set<FullStructureField> getFieldsToRefresh() {
        return fieldsToRefresh;
    }

	public List<StructureChildInverseRelation> getChildren() {
		return children;
	}

	public void setChildren(List<StructureChildInverseRelation> children) {
		this.children = children;
	}

	public List<StructurePersonInverseRelation> getPersons() {
		return persons;
	}

	public void setPersons(List<StructurePersonInverseRelation> persons) {
		this.persons = persons;
	}

	public List<SpinoffFromInverseRelation> getSpinoffFrom() {
		return spinoffFrom;
	}

	public void setSpinoffFrom(List<SpinoffFromInverseRelation> spinoffFrom) {
		this.spinoffFrom = spinoffFrom;
	}

	public List<Website> getWebsites() {
		return websites;
	}

	public void setWebsites(List<Website> websites) {
		this.websites = websites;
	}

	public List<StructureProjectInverseRelation> getProjects() {
		return projects;
	}

	public void setProjects(List<StructureProjectInverseRelation> projects) {
		this.projects = projects;
	}

	public List<GraphElement> getGraph() {
		return graph;
	}

	public void setGraph(List<GraphElement> graph) {
		this.graph = graph;
	}

	public void setFieldsToRefresh(Set<FullStructureField> fieldsToRefresh) {
		this.fieldsToRefresh = fieldsToRefresh;
	}

	public List<StructurePublicationInverseRelation> getPublications() {
		return publications;
	}

	public void setPublications(List<StructurePublicationInverseRelation> publications) {
		this.publications = publications;
	}
}
