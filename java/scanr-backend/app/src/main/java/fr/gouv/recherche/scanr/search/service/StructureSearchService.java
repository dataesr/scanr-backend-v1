/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.search.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.elasticsearch.action.search.ClearScrollRequest;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MoreLikeThisQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.sword.utils.elasticsearch.exceptions.EsSerializationException;
import com.sword.utils.elasticsearch.intf.IIdentifiable;

import fr.gouv.recherche.scanr.config.elasticsearch.EsIndexEnum;
import fr.gouv.recherche.scanr.db.model.Address;
import fr.gouv.recherche.scanr.db.model.Structure;
import fr.gouv.recherche.scanr.db.model.full.FullStructure;
import fr.gouv.recherche.scanr.search.model2.request.LikeRequest;
import fr.gouv.recherche.scanr.search.model2.request.SearchRequest;
import fr.gouv.recherche.scanr.search.model2.response.FacetResult;
import fr.gouv.recherche.scanr.search.model2.response.LikeResponse;
import fr.gouv.recherche.scanr.search.model2.response.SearchResponse;
import fr.gouv.recherche.scanr.search.model2.response.SearchResult;
import fr.gouv.recherche.scanr.util.BoostedSearchFieldsMapperInterface;
import fr.gouv.recherche.scanr.util.StructureBoostedSearchFieldsMapper;

@Service
public class StructureSearchService extends AbstractSearchService {

    private static final Logger log = LoggerFactory.getLogger(StructureSearchService.class);
    private String lang = SearchRequest.DEFAULT_LANG;

    public static final String[] FETCH_GEO = new String[]{
            FullStructure.FIELDS.ID,
            FullStructure.FIELDS.LABEL,
            FullStructure.FIELDS.ACRONYM,
            FullStructure.FIELDS.ADDRESS.GPS
    };

    private static final String[] FETCH_EXPORT = {
            FullStructure.FIELDS.ID,
            FullStructure.FIELDS.LABEL,
            FullStructure.FIELDS.ACRONYM,
            FullStructure.FIELDS.KIND,
            FullStructure.FIELDS.ALIAS,
            FullStructure.FIELDS.NATURE,
            FullStructure.FIELDS.LEVEL,
            FullStructure.FIELDS.WEBSITES.TITLE,
            FullStructure.FIELDS.ADDRESS.POSTCODE,
            FullStructure.FIELDS.ADDRESS.CITY,
            FullStructure.FIELDS.ADDRESS.URBAN_UNIT_CODE,
            FullStructure.FIELDS.ADDRESS.GPS,
    };

    /**
     * Effectue la recherche Elasticsearch à partir des données de la requête et les transforme au format souhaité
     *
     * @param searchRequest
     * @return
     * @throws IOException 
     */
    public SearchResponse<FullStructure> search(SearchRequest searchRequest) throws IOException {

        SearchSourceBuilder searchBuilder = buildSearchBuilder(searchRequest, true);
        org.elasticsearch.action.search.SearchResponse elasticSearchResponse = elasticsearchService.getEsClient().getEsRestClient()
        		.search(new org.elasticsearch.action.search.SearchRequest(EsIndexEnum.STRUCTURE.getName()).source(searchBuilder), RequestOptions.DEFAULT);

        SearchResponse APIResponse = buildScanESRSearchResponse(searchRequest, elasticSearchResponse);

        return APIResponse;
    }

    /**
     * Effectue la recherche Elasticsearch à partir des données de la requête et les transforme au format souhaité
     *
     * @param searchRequest
     * @return
     * @throws IOException 
     */
    public SearchResponse<FullStructure> searchExport(SearchRequest searchRequest, int searchSizeLimit) throws IOException {
        SearchSourceBuilder searchBuilder = buildSearchBuilder(searchRequest, false);
        searchBuilder.size(searchSizeLimit);

        searchBuilder.fetchSource(FETCH_EXPORT, null);

        org.elasticsearch.action.search.SearchResponse elasticSearchResponse = elasticsearchService.getEsClient().getEsRestClient()
        		.search(new org.elasticsearch.action.search.SearchRequest(EsIndexEnum.STRUCTURE.getName()).source(searchBuilder), RequestOptions.DEFAULT);
        
        SearchResponse APIResponse = buildScanESRSearchResponse(searchRequest, elasticSearchResponse);

        return APIResponse;
    }

    /**
     * Effectue la recherche Elasticsearch à partir des données de la requête et les transforme au format souhaité
     *
     * @param searchRequest
     * @return
     * @throws IOException 
     */
    public SearchResponse geoSearch(SearchRequest searchRequest) throws IOException {
        SearchSourceBuilder searchBuilder = buildSearchBuilder(searchRequest, false);
        searchBuilder.fetchSource(FETCH_GEO, null);

        SearchResponse APIResponse = new SearchResponse(searchRequest);
        Collection<SearchResult<FullStructure>> searchResults = scrollRequest(searchBuilder, searchRequest.getPageSize());
        APIResponse.setResults(searchResults);
        APIResponse.setTotal(searchResults.size());

        return APIResponse;
    }

    /**
     * Effectue la recherche Elasticsearch à partir des données de la requête et les transforme au format souhaité
     * @param id
     * @param distance
     * @param nb
     * @return
     */
    public List<Structure> nearSearch(String id, double distance, int nb) throws IOException {
        FullStructure structure = null;
        List<Structure> structures = new LinkedList<>();

        GeoPoint gps = null;
        try {
            structure = elasticsearchService.getEsClient().getServiceStructure().get(id);
            Optional<Address> mainAddress = structure.getMainAddressList().stream().findFirst();
            if (mainAddress.isPresent()) {
                gps = mainAddress.get().getGps();
            }
        }
        catch (Exception e) {
            throw new IOException("Cannot get address GPS from structure with id " + id);
        }

        if (gps == null) {
            throw new IOException("Cannot get address GPS from structure with id " + id);
        }

        SearchSourceBuilder searchBuilder = buildNearRequestBuilder(gps, distance, nb);
        org.elasticsearch.action.search.SearchResponse response = elasticsearchService.getEsClient().getEsRestClient()
        		.search(new org.elasticsearch.action.search.SearchRequest(EsIndexEnum.STRUCTURE.getName()).source(searchBuilder), RequestOptions.DEFAULT);

        response.getHits().forEach(hit -> structures.add(buildFullStructure(hit)));

        return structures;
    }

    /**
     * @param searchRequest
     * @param addAggregationsAndHighlights
     * @return
     */
    private SearchSourceBuilder buildSearchBuilder(SearchRequest searchRequest, boolean addAggregationsAndHighlights) {
        // Lang
        if (searchRequest.getLang() != null && !searchRequest.getLang().isEmpty()) {
            lang = searchRequest.getLang();
        }

        // Requête Elasticsearch de recherche
        BoolQueryBuilder query = queryBuilderService.buildQuery(searchRequest, boostedSearchFieldsMapper, relatedModel);

        // Facettes
        List<AggregationBuilder> aggs = new ArrayList<>();

        if (addAggregationsAndHighlights) {
            aggs = aggregationService.buildAggregations(searchRequest, boostedSearchFieldsMapper, relatedModel, lang);

            // Si pas de facette demandée, on prend les facettes pré-définies
            if (aggs.isEmpty()) {
                aggs.addAll(aggregationService.getPrebuiltAggregations(lang));
            }
        }

        // Exécution de la recherche ES
        SearchSourceBuilder searchBuilder = new SearchSourceBuilder();
        searchBuilder.query(query);

        // Page et PageSize
        searchBuilder.from(searchRequest.getPage() * searchRequest.getPageSize());
        searchBuilder.size(searchRequest.getPageSize());
        searchBuilder.trackTotalHits(true);

        // Sort
        ArrayList<SortBuilder<?>> sorts = buildSort(searchRequest.getSort(), searchRequest.getLang(), getRelatedModel());
        if (sorts != null) {
        	searchBuilder.sort(sorts);
        }

        // SourceFields
        if (searchRequest.getSearchFields() == null || searchRequest.getSourceFields().isEmpty()) {
            searchBuilder.fetchSource(defaultSourceFields.toArray(new String[0]), null);
        } else {
            String[] sourceFields = searchRequest.getSourceFields().toArray(new String[0]);
            searchBuilder.fetchSource(sourceFields, null);
        }

        // Setup highlighting
        List<String> searchFields;
        if (searchRequest.getSearchFields() != null && !searchRequest.getSearchFields().isEmpty()) {
            searchFields = searchRequest.getSearchFields();
        }
        else {
            searchFields = new ArrayList<>(boostedSearchFieldsMapper.getBoostConfiguration().keySet());
        }
        HighlightBuilder highlightBuilder = buildHighlight(searchFields, new FullStructure());
        searchBuilder.highlighter(highlightBuilder);

        // Add aggregations to requestBuilder
        for (AggregationBuilder agg : aggs) {
            searchBuilder.aggregation(agg);
        }

        return searchBuilder;
    }

    /**
     * @param gps
     * @param distance
     * @param nb
     * @return
     */
    private SearchSourceBuilder buildNearRequestBuilder(GeoPoint gps, double distance, int nb) {

        // Requête Elasticsearch de recherche
        BoolQueryBuilder query = queryBuilderService.buildNearQuery(gps, distance);

        // Exécution de la recherche ES
        SearchSourceBuilder searchBuilder = new SearchSourceBuilder();
        searchBuilder.query(query);
        searchBuilder.size(nb);
        searchBuilder.fetchSource(defaultSourceFields.toArray(new String[0]), null);

        return searchBuilder;
    }

    /**
     * Génère la réponse à renvoyer dans l'API à partir de la réponse Elasticsearch
     *
     * @param request
     * @param response
     * @return
     */
    protected SearchResponse<FullStructure> buildScanESRSearchResponse(SearchRequest request, org.elasticsearch.action.search.SearchResponse response) {
        long total = response.getHits().getTotalHits().value;
        Collection<SearchResult> results = getSearchResultsFromElasticsearchResponse(response);
        SearchResponse<FullStructure> scanESRResponse = new SearchResponse(request, total, results);

        // Aggregations vers Histograms pour la Response
        if (response.getAggregations() != null) {
            List<FacetResult> facets = buildFacets(response.getAggregations());
            scanESRResponse.setFacets(facets);
        }

        return scanESRResponse;
    }

    /**
     * @param response
     * @return
     */
    private Collection<SearchResult> getSearchResultsFromElasticsearchResponse(org.elasticsearch.action.search.SearchResponse response) {
        Collection<SearchResult> results = new ArrayList<>();

        for (SearchHit hit : response.getHits().getHits()) {
            SearchResult searchResult = new SearchResult<>(buildFullStructure(hit));

            // Ajout des highlights
            if (!hit.getHighlightFields().isEmpty()) {
                List<SearchResult.HighlightItem> highlightItems = new ArrayList<>();
                for (String field : hit.getHighlightFields().keySet()) {
                    SearchResult.HighlightItem highlightItem = new SearchResult.HighlightItem(field, hit.getHighlightFields().get(field).getFragments()[0].string());
                    highlightItems.add(highlightItem);
                }
                searchResult.setHighlights(highlightItems);
            }

            results.add(searchResult);
        }
        return results;
    }

    /**
     * Crée des FullStructure à partir d'un SearchHit Elasticsearch
     *
     * @param hit
     * @return
     */
    private FullStructure buildFullStructure(SearchHit hit) {
        try {
            return objectMapper.readValue(hit.getSourceAsString(), FullStructure.class);
        } catch (IOException e) {
            throw new EsSerializationException("Impossible to deserialize from json", e);
        }
    }

    /**
     * @param searchRequestBuilder
     * @return
     * @throws IOException 
     */
    private Collection<SearchResult<FullStructure>> scrollRequest(SearchSourceBuilder searchBuilder, int maxResult) throws IOException {
        searchBuilder.from(0).size(MAX_FOR_SCROLL);
        searchBuilder.trackTotalHits(true);

        Collection<SearchResult<FullStructure>> searchResults = new ArrayList<>();
        org.elasticsearch.action.search.SearchResponse response = elasticsearchService.getEsClient().getEsRestClient().search(
        		new org.elasticsearch.action.search.SearchRequest(EsIndexEnum.STRUCTURE.getName())
	        		.scroll(TimeValue.timeValueMillis(SCROLL_TIMEOUT))
	        		.source(searchBuilder), 
	        	RequestOptions.DEFAULT);
        
        response.getHits().forEach(hit -> {
            searchResults.add(new SearchResult<>(buildFullStructure(hit)));
        });

        long currentNbResult = response.getHits().getTotalHits().value;
        while (maxResult == 0 || currentNbResult < maxResult) {
        	response = elasticsearchService.getEsClient().getEsRestClient().scroll(
            		new SearchScrollRequest(response.getScrollId()).scroll(TimeValue.timeValueMillis(SCROLL_TIMEOUT)), 
    	        	RequestOptions.DEFAULT);

            log.trace("Fetched" + response.getHits().getHits().length + " elements");

            //Break condition: No hits are returned
            if (response.getHits().getHits().length == 0) {
            	ClearScrollRequest clearScrollRequest = new ClearScrollRequest();
            	clearScrollRequest.addScrollId(response.getScrollId());
            	elasticsearchService.getEsClient().getEsRestClient().clearScroll(clearScrollRequest, RequestOptions.DEFAULT);
                break;
            }

            currentNbResult += response.getHits().getTotalHits().value;

            response.getHits().forEach(hit -> {
                searchResults.add(new SearchResult<>(buildFullStructure(hit)));
            });
        }
        return searchResults;
    }

    /**
     * Construction des queries d'exclusion pour les Structure
     *
     * No longer used
     *
     * @return
     */
    private List<QueryBuilder> getStructureForcedFilterQueries() {
        List<QueryBuilder> queries = new ArrayList<>();

        // On ne veut que les Structures françaises
        QueryBuilder isFrenchQuery = QueryBuilders.termQuery(FullStructure.FIELDS.IS_FRENCH, true);
        queries.add(isFrenchQuery);

        // On ne veut que les Structures actives (non supprimées)
        QueryBuilder isOldQuery = QueryBuilders.termQuery(FullStructure.FIELDS.STATUS, FullStructure.FIELDS.STATUS_ACTIVE);
        queries.add(isOldQuery);

        return queries;
    }

    @Override
    protected BoostedSearchFieldsMapperInterface getBoostedSearchFieldsMapper() {
        return new StructureBoostedSearchFieldsMapper();
    }

    @Override
    protected IIdentifiable getRelatedModel() {
        return new FullStructure();
    }

    @Override
    public void setAggregationService() {
        this.aggregationService = new StructureAggregationService(getRelatedModel(), lang);
    }

    @Override
    protected void setDefaultSourceFields() {
        List<String> defaultSourceFields = new ArrayList<>();
        defaultSourceFields.add(FullStructure.FIELDS.ID);
        defaultSourceFields.add(FullStructure.FIELDS.LABEL);
        defaultSourceFields.add(FullStructure.FIELDS.ACRONYM);
        defaultSourceFields.add(FullStructure.FIELDS.NATURE);
        defaultSourceFields.add(FullStructure.FIELDS.INSTITUTIONS.CODE);
        defaultSourceFields.add(FullStructure.FIELDS.ADDRESS.TITLE);
        defaultSourceFields.add(FullStructure.FIELDS.KIND);
        defaultSourceFields.add(FullStructure.FIELDS.LEVEL);
        defaultSourceFields.add(FullStructure.FIELDS.IS_FRENCH);

        this.defaultSourceFields = defaultSourceFields;
    }

    protected void setDefaultFields() {
        List<String> defaultFields = new ArrayList<>();
        defaultFields.add(FullStructure.FIELDS.LABEL);
        defaultFields.add(FullStructure.FIELDS.ACRONYM);
        defaultFields.add(FullStructure.FIELDS.ALIAS);

        this.defaultFields = defaultFields;
    }

    /**
     * Recherche More like this d'elastic
     * @see "https://www.elastic.co/guide/en/elasticsearch/reference/6.7/query-dsl-mlt-query.html"
     * @param likeRequest
     * @return
     * @throws IOException 
     */
    public LikeResponse<FullStructure> moreLikeThis(LikeRequest likeRequest) throws IOException {

        // SourceFields
        List<String> fieldsSelected;
        if (likeRequest.getFields().isEmpty()) {
            fieldsSelected = defaultFields;
        } else {
            fieldsSelected = likeRequest.getFields();
        }
        fieldsSelected = queryBuilderService.getFieldsWithLanguage(fieldsSelected, likeRequest.getLang(), relatedModel);
        String[] fields = fieldsSelected.toArray(new String[0]);

        // Create items
        MoreLikeThisQueryBuilder.Item[] likeIds = new MoreLikeThisQueryBuilder.Item[likeRequest.getLikeIds().size()];
        for (int i = 0; i < likeRequest.getLikeIds().size(); i++) {
            String id = likeRequest.getLikeIds().get(i);
            MoreLikeThisQueryBuilder.Item item = new MoreLikeThisQueryBuilder.Item(EsIndexEnum.STRUCTURE.getName(), id);
            likeIds[i] = item;
        }

        MoreLikeThisQueryBuilder moreLikeThisQueryBuilderyBuilder = new MoreLikeThisQueryBuilder(fields, likeRequest.getLikeTexts().toArray(new String[0]), likeIds);
        moreLikeThisQueryBuilderyBuilder = moreLikeThisQueryBuilderyBuilder.minTermFreq(1);
        BoolQueryBuilder query = new BoolQueryBuilder();
        query = query.must(moreLikeThisQueryBuilderyBuilder);

        // Exécution de la recherche ES
        SearchSourceBuilder searchBuilder = new SearchSourceBuilder();
        searchBuilder.query(query);

        // Page et PageSize
        searchBuilder.from(likeRequest.getPage() * likeRequest.getPageSize());
        searchBuilder.size(likeRequest.getPageSize());
        searchBuilder.trackTotalHits(true);

        // SourceFields
        searchBuilder.fetchSource(defaultSourceFields.toArray(new String[0]), null);

        // Add aggregations to requestBuilder
        for (AggregationBuilder agg : aggregationService.getPrebuiltAggregations(lang)) {
            searchBuilder.aggregation(agg);
        }

        org.elasticsearch.action.search.SearchResponse elasticSearchResponse = elasticsearchService.getEsClient().getEsRestClient()
        		.search(new org.elasticsearch.action.search.SearchRequest(EsIndexEnum.STRUCTURE.getName()).source(searchBuilder), RequestOptions.DEFAULT);

        long total = elasticSearchResponse.getHits().getTotalHits().value;
        Collection<SearchResult> results = new ArrayList<>();

        for (SearchHit hit : elasticSearchResponse.getHits().getHits()) {
            SearchResult searchResult = new SearchResult<FullStructure>();
            FullStructure fullStructure = buildFullStructure(hit);
            searchResult.setValue(fullStructure);
            results.add(searchResult);
        }
        LikeResponse<FullStructure> scanESRResponse = new LikeResponse(likeRequest, total, results);

        // Aggregations vers Histograms pour la Response
        if (elasticSearchResponse.getAggregations() != null) {
            List<FacetResult> facets = buildFacets(elasticSearchResponse.getAggregations());
            scanESRResponse.setFacets(facets);
        }

        return scanESRResponse;
    }
}
