/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */

package fr.gouv.recherche.scanr.db.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sword.utils.elasticsearch.intf.IIdentifiable;
import io.swagger.annotations.ApiModelProperty;

/**
 * Used for similarProjects/Persons/Publications
 * @author mdutoo
 *
 * @param <T>
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class SimilarTypedObject<T extends IIdentifiable> {
	
	@ApiModelProperty("Light fields in Full object, else only id")
	private T target;
	// relation fields :
	private String score; // and not integer
	
	public T getTarget() {
		return target;
	}
	public void setTarget(T target) {
		this.target = target;
	}
	public String getScore() {
		return score;
	}
	public void setScore(String score) {
		this.score = score;
	}
}
