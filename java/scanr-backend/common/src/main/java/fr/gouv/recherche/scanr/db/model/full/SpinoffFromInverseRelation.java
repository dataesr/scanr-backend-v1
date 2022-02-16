/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */

package fr.gouv.recherche.scanr.db.model.full;

import com.fasterxml.jackson.annotation.JsonInclude;
import fr.gouv.recherche.scanr.db.model.Project;
import fr.gouv.recherche.scanr.db.model.Structure;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value="v2.SpinoffFromInverseRelation", description="Inverse relation from spinned to spinner Structure.")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class SpinoffFromInverseRelation {
	
	@ApiModelProperty(value="Structure that did the spinoff. NB. pas multivaluée (car Structure.spinoffs.project ne l'est pas)")
    private Structure structure;
	// relation fields :
	// values of Structure fields as provided in the object that is source of the relation :
    private String type;
    
	public Structure getStructure() {
		return structure;
	}
	public void setStructure(Structure structure) {
		this.structure = structure;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}

}
