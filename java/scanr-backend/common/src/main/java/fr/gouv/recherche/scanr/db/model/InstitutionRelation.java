/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.db.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSetter;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Relation to institution
 * ex. for tutelles
 * The name/type of the relation is "code"
 */
@ApiModel("v2.Institution")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class InstitutionRelation {
	
	@ApiModelProperty(value="At least identifier of the institution")
	private Structure structure;
	// values of Structure fields as provided in the object that is source of the relation :
    @ApiModelProperty(value="Full label", example="Institut national de la recherche agronomique")
    private String label;
    @ApiModelProperty("URL to the institution base website (optional)")
    private String url;

    // relation fields :
    @ApiModelProperty("Association code between the host structure and this institution, e.g. UMR 8896")
    private AssociationCode code;
    @ApiModelProperty(value="The association type between the structure and the institution ex. Tutelles, participant", example="Etablissement support")
    private String relationType;
    @ApiModelProperty("Year of the association")
    private Date fromDate;

    public InstitutionRelation() {
    }


    @ApiModel("v2.AssociationCode")
    public static class AssociationCode {
    	@ApiModelProperty(example="UMR")
        private String type;
    	@ApiModelProperty(example="8896")
        private String number;
    	@ApiModelProperty(example="UMR 8896") // TODO remove save if required by ScanESR (computed)
        private String normalized;

        public AssociationCode(String type, String number) {
            this.type = type.toUpperCase();
            this.number = number;
        }

        protected static String normalize(String type, String number) {
            type = type.replaceAll("_.*", "");
            return number != null ? type+" "+number.replaceAll("^0*", "") : null;
        }

        public AssociationCode(String code) {
            String[] split = code.trim().split("\\s+");
            assert split.length == 2;
            type = split[0].toUpperCase();
            number = split[1];
        }

        public AssociationCode() {
        }

        public String getType() {
            return type;
        }

        public String getNumber() {
            return number;
        }

        public String getNormalized() {
            return normalized;
        }

        public void normalize() {
            normalized = normalize(type, number);
        }

        @Override
        public String toString() {
            return type+" "+number;
        }
    }

	public Structure getStructure() {
		return structure;
	}
	public void setStructure(Structure structure) {
		this.structure = structure;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public AssociationCode getCode() {
		return code;
	}
	public void setCode(AssociationCode code) {
		this.code = code;
	}
	public String getRelationType() {
		return relationType;
	}
	public void setRelationType(String relationType) {
		this.relationType = relationType;
	}
	public Date getFromDate() {
		return fromDate;
	}
	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}
    
}
