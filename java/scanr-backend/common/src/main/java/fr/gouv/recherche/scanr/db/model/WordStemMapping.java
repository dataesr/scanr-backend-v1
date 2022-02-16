/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.db.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;
import java.util.Map;

/**
 * For a structure, stores the mapping between stem and most frequent corresponding word
 */
@Document
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class WordStemMapping {

    /**
     * Id of the structure.
     */
    @Id
    private String id;

    /**
     * mapping between stem and most frequent corresponding word
     */
    protected Map<String, String> stemToWord = new HashMap<>();

    public WordStemMapping() {
    }

    public WordStemMapping(String id, Map<String, String> stemToWord) {
        this.id = id;
        this.stemToWord = stemToWord;
    }

    public String mapStem(String stem) {
        return stemToWord.getOrDefault(stem, stem);
    }

    public int stemSize() {
        return stemToWord.size();
    }
}
