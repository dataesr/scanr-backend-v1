/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */

package fr.gouv.recherche.scanr.db.model.full;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import fr.gouv.recherche.scanr.db.model.Structure;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value="v2.StructureChildInverseRelation", description="Inverse relation of Structure.parents, in Full only")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class StructureChildInverseRelation {
	
    private Structure structure;
    // original relation fields :
    private Date fromDate;
    @ApiModelProperty("Type of parent relation (including whether exclusive)")
    private String relationType;

	public StructureChildInverseRelation() {
	}

	public StructureChildInverseRelation(Structure structure) {
    	this.structure = structure;
    }

    public Structure getStructure() {
		return structure;
	}
	public void setStructure(Structure structure) {
		this.structure = structure;
	}
	public Date getFromDate() {
		return fromDate;
	}
	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}
	public String getRelationType() {
		return relationType;
	}
	public void setRelationType(String relationType) {
		this.relationType = relationType;
	}

}
