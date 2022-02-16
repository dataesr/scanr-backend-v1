/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.search.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import javax.annotation.PostConstruct;

import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.ScoreSortBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sword.utils.elasticsearch.intf.IIdentifiable;

import fr.gouv.recherche.scanr.config.elasticsearch.EsClient;
import fr.gouv.recherche.scanr.search.model2.request.SearchRequest;
import fr.gouv.recherche.scanr.search.model2.response.FacetResult;
import fr.gouv.recherche.scanr.search.model2.response.SearchResponse;
import fr.gouv.recherche.scanr.service.ElasticsearchService;
import fr.gouv.recherche.scanr.util.BoostedSearchFieldsMapperInterface;
import fr.gouv.recherche.scanr.util.ScanESRReflectionUtils;

public abstract class AbstractSearchService {

    protected static final int MAX_FOR_SCROLL = 5000;
    protected static final int SCROLL_TIMEOUT = 120000;
    protected static final String HIGHLIGHT_PRE = "<strong>";
    protected static final String HIGHLIGHT_POST = "</strong>";
    protected static final int HIGHLIGHT_FRAGMENT_SIZE = 100;
    protected static final String HIGHLIGHTER_TYPE = "fvh";
    protected static final String SCORE_SEARCH_SORT = "_score";
    protected static final String DEFAULT_SEARCH_SORT = SCORE_SEARCH_SORT;

    protected List<String> defaultSourceFields;
    protected List<String> defaultFields;
    protected BoostedSearchFieldsMapperInterface boostedSearchFieldsMapper;
    protected AggregationService aggregationService;

    @Autowired
    protected ElasticsearchService elasticsearchService;

    @Autowired
    protected QueryBuilderService queryBuilderService;

    @Autowired
    protected FacetService facetService;

    protected ObjectMapper objectMapper;

    public IIdentifiable relatedModel;

    @PostConstruct
    public void init() {
        this.boostedSearchFieldsMapper = getBoostedSearchFieldsMapper();
        setDefaultSourceFields();
        objectMapper = new ObjectMapper();
        setAggregationService();
        this.relatedModel = getRelatedModel();
    }

    public abstract SearchResponse search(SearchRequest searchRequest) throws IOException;

    protected abstract SearchResponse buildScanESRSearchResponse(SearchRequest request, org.elasticsearch.action.search.SearchResponse response);

    protected List<FacetResult> buildFacets(Aggregations aggregations) {
        return facetService.buildFacetResults(aggregations);
    }

    protected abstract BoostedSearchFieldsMapperInterface getBoostedSearchFieldsMapper();

    protected abstract IIdentifiable getRelatedModel();

    protected void setDefaultSourceFields() {
        this.defaultSourceFields = new ArrayList<>();
    }

    public void setAggregationService() {
        this.aggregationService = null;
    }

    protected void setDefaultFields() {
        this.defaultFields = new ArrayList<>();
    }

    /**
     * Build Elasticsearch sorts from request
     *
     * @param requestSorts
     * @return
     */
    protected ArrayList<SortBuilder<?>> buildSort(LinkedHashMap<String, SearchRequest.SortDirection> requestSorts, String lang, IIdentifiable relatedModel) {
        ArrayList<SortBuilder<?>> sorts = new ArrayList<>();

        if (requestSorts == null) {
            ScoreSortBuilder sort = SortBuilders.scoreSort();
            sort.order(SortOrder.DESC);
            sorts.add(sort);

            return sorts;
        }

        requestSorts.forEach((key, value) -> {
            // If not null and not _score -> field.keyword, otherwise _score
            SortBuilder<?> sort;
            if (key != null && !key.equals(DEFAULT_SEARCH_SORT)) {
                String field = queryBuilderService.getFieldsWithLanguage(Collections.singletonList(key), lang, relatedModel).get(0);
                // No .sort subfield if Date, Number or Boolean
                field = ScanESRReflectionUtils.isFieldNumberDateOrBoolean(relatedModel, field) ? field : field + EsClient.SORT_SUFFIXE;

                sort = SortBuilders.fieldSort(field);
            }
            else {
                sort = SortBuilders.scoreSort();
            }
            // If value is ASC -> ASC, otherwise DESC
            sort.order(value != null && value == SearchRequest.SortDirection.ASC ? SortOrder.ASC : SortOrder.DESC);

            sorts.add(sort);
        });

        return sorts;
    }

    /**
     * @param searchFields
     * @param relatedModel
     * @return
     */
    protected HighlightBuilder buildHighlight(List<String> searchFields, IIdentifiable relatedModel) {

        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder = highlightBuilder.preTags(HIGHLIGHT_PRE);
        highlightBuilder = highlightBuilder.postTags(HIGHLIGHT_POST);
        highlightBuilder = highlightBuilder.highlighterType(HIGHLIGHTER_TYPE);
        highlightBuilder = highlightBuilder.fragmentSize(HIGHLIGHT_FRAGMENT_SIZE);

        for (String field : searchFields) {
            if (ScanESRReflectionUtils.isFieldTranslatable(relatedModel, field)) {
                field += ".*";
            }
            highlightBuilder = highlightBuilder.field(field);
        }

        return highlightBuilder;
    }
}
