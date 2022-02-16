/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.db.model.full;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;
import fr.gouv.recherche.scanr.db.model.Structure;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Relationships toward other structures.
 * One GraphElement by structure related to the FullStructure.
 * A relationship is created when other structures have common projects, publications...
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@ApiModel(value="v2.Website", description="Same as v1")
public class GraphElement {

    @ApiModelProperty("target Light Structure of the relationship")
    private Structure structure;
    @ApiModelProperty("scores of this relationship")
    private Map<String, Integer> details = new HashMap<>();

    private int weight;

    public GraphElement() {
    }

    public int getWeight() {
        return details.values().stream().mapToInt(Integer::intValue).sum();
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public Structure getStructure() {
        return structure;
    }

    public void addElement(String type) {
        Integer value = details.getOrDefault(type, 0) + 1;
        details.put(type, value);
    }

    public Map<String, Integer> getDetails() {
        return details;
    }

    public void setStructure(Structure structure) {
        this.structure = structure;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GraphElement that = (GraphElement) o;
        return Objects.equals(structure, that.structure) &&
                Objects.equals(details, that.details);
    }

    @Override
    public int hashCode() {
        return Objects.hash(structure, details);
    }
}
