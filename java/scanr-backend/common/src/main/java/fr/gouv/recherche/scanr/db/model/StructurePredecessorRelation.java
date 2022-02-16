/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.db.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSetter;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 
 * TODO or not a relation because obsolete Structures are not in the database anymore ??
 */
@ApiModel(value="v2.StructurePredecessorRelation", description="Historical modification of a research structure.")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class StructurePredecessorRelation {
    @ApiModelProperty("Structure impacted by the event, if known only id outside Full")
    private Structure structure;
    // relation fields :
    @ApiModelProperty(value="Year of the event", example="2008")
    private Integer eventYear; // rather than date
    @ApiModelProperty(value="Type of the event", example="fusion")
    private String eventType; // rather than event
    // values of Structure fields as provided in the Publication object that is source of the relation :
    @ApiModelProperty("If id of structure not found")
    private String label;

    public StructurePredecessorRelation(String id, Integer eventYear, String eventType, String label) {
    	if (id != null && id.length() != 0) {
    		this.structure = new Structure();
    		this.structure.setId(id);
    	}
        this.eventYear = eventYear;
        this.eventType = eventType;
        this.label = label;
    }

    public StructurePredecessorRelation() {
    }

	public Structure getStructure() {
		return structure;
	}

	public void setStructure(Structure structure) {
		this.structure = structure;
	}

	public Integer getEventYear() {
		return eventYear;
	}

	public void setEventYear(Integer eventYear) {
		this.eventYear = eventYear;
	}

	public String getEventType() {
		return eventType;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
    
}
