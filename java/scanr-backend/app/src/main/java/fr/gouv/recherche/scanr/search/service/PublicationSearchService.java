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
import java.util.NoSuchElementException;
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
import fr.gouv.recherche.scanr.db.model.full.FullPublication;
import fr.gouv.recherche.scanr.db.model.full.FullStructure;
import fr.gouv.recherche.scanr.search.model2.request.LikeRequest;
import fr.gouv.recherche.scanr.search.model2.request.SearchRequest;
import fr.gouv.recherche.scanr.search.model2.response.FacetResult;
import fr.gouv.recherche.scanr.search.model2.response.LikeResponse;
import fr.gouv.recherche.scanr.search.model2.response.SearchResponse;
import fr.gouv.recherche.scanr.search.model2.response.SearchResult;
import fr.gouv.recherche.scanr.util.BoostedSearchFieldsMapperInterface;
import fr.gouv.recherche.scanr.util.PublicationBoostedSearchFieldsMapper;

@Service
public class PublicationSearchService extends AbstractSearchService {

    private static final Logger log = LoggerFactory.getLogger(PublicationSearchService.class);
    private String lang = SearchRequest.DEFAULT_LANG;

    public static final String[] FETCH_GEO = new String[]{
            FullPublication.FIELD_ID,
            FullPublication.FIELD_TITLE,
            FullPublication.FIELD_AFFILIATIONS + "." + FullStructure.FIELDS.LABEL,
            FullPublication.FIELD_AFFILIATIONS + "." + FullStructure.FIELDS.ADDRESS.GPS
    };

    /**
     * Effectue la recherche Elasticsearch à partir des données de la requête et les transforme au format souhaité
     *
     * @param searchRequest
     * @return
     * @throws IOException 
     */
    public SearchResponse<FullPublication> search(SearchRequest searchRequest) throws IOException {
        SearchSourceBuilder searchBuilder = buildSearchBuilder(searchRequest);
        org.elasticsearch.action.search.SearchResponse elasticSearchResponse = elasticsearchService.getEsClient().getEsRestClient()
        		.search(new org.elasticsearch.action.search.SearchRequest(EsIndexEnum.PUBLICATION.getName()).source(searchBuilder), RequestOptions.DEFAULT);

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
    public SearchResponse<FullPublication> searchExport(SearchRequest searchRequest, int searchSizeLimit) throws IOException {
        SearchSourceBuilder searchBuilder = buildSearchBuilder(searchRequest);
        searchBuilder.size(searchSizeLimit);

        List<String> sourceFields = new ArrayList<>();
        sourceFields.add(FullPublication.FIELD_TITLE);
        sourceFields.add(FullPublication.FIELD_SUBTITLE);
        sourceFields.add(FullPublication.FIELD_SUMMARY);
        sourceFields.add(FullPublication.FIELD_AUTHORS_FULLNAME);
        sourceFields.add(FullPublication.FIELD_SOURCE_TITLE);
        sourceFields.add(FullPublication.FIELD_SOURCE_SUBTITLE);
        sourceFields.add(FullPublication.FIELD_SOURCE_ISSUE);
        sourceFields.add(FullPublication.FIELD_IS_OA);
        sourceFields.add(FullPublication.FIELD_SOURCE_PUBLISHER);
        sourceFields.add(FullPublication.FIELD_SUBMISSION_DATE);
        sourceFields.add(FullPublication.FIELD_PUBLICATION_DATE);
        sourceFields.add(FullPublication.FIELD_TYPE);
        sourceFields.add(FullPublication.FIELD_AWARDS_LABEL);
        sourceFields.add(FullPublication.FIELD_IS_INTERNATIONAL);
        sourceFields.add(FullPublication.FIELD_IS_OEB);
        sourceFields.add(FullPublication.FIELD_GRANTED_DATE);
        searchBuilder.fetchSource(sourceFields.toArray(new String[0]), null);

        org.elasticsearch.action.search.SearchResponse elasticSearchResponse = elasticsearchService.getEsClient().getEsRestClient()
        		.search(new org.elasticsearch.action.search.SearchRequest(EsIndexEnum.PUBLICATION.getName()).source(searchBuilder), RequestOptions.DEFAULT);

        SearchResponse APIResponse = buildScanESRSearchResponse(searchRequest, elasticSearchResponse);

        return APIResponse;
    }

    private SearchSourceBuilder buildSearchBuilder(SearchRequest searchRequest) {
        // Lang
        if (searchRequest.getLang() != null && !searchRequest.getLang().isEmpty()) {
            lang = searchRequest.getLang();
        }

        // Requête Elasticsearch de recherche
        QueryBuilder query = queryBuilderService.buildQuery(searchRequest, boostedSearchFieldsMapper, relatedModel);

        // Facettes
        List<AggregationBuilder> aggs = aggregationService.buildAggregations(searchRequest, boostedSearchFieldsMapper, relatedModel, lang);

        // Si pas de facette demandée, on prend les facettes pré-définies
        if (aggs.isEmpty()) {
            aggs.addAll(aggregationService.getPrebuiltAggregations(lang));
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
        if (searchRequest.getSourceFields().isEmpty()) {
            searchBuilder.fetchSource(defaultSourceFields.toArray(new String[0]), null);
        } else {
            String[] sourceFields = searchRequest.getSourceFields().toArray(new String[0]);
            searchBuilder.fetchSource(sourceFields, null);
        }

        // Add aggregations to requestBuilder
        for (AggregationBuilder agg : aggs) {
            searchBuilder.aggregation(agg);
        }

        // Setup highlighting
        List<String> searchFields;
        if (searchRequest.getSearchFields() != null && !searchRequest.getSearchFields().isEmpty()) {
            searchFields = searchRequest.getSearchFields();
        }
        else {
            searchFields = new ArrayList<>(boostedSearchFieldsMapper.getBoostConfiguration().keySet());
        }
        HighlightBuilder highlightBuilder = buildHighlight(searchFields, new FullPublication());
        searchBuilder.highlighter(highlightBuilder);

        return searchBuilder;
    }

    /**
     * Génère la réponse à renvoyer dans l'API à partir de la réponse Elasticsearch
     *
     * @param request
     * @param response
     * @return
     */
    protected SearchResponse buildScanESRSearchResponse(SearchRequest request, org.elasticsearch.action.search.SearchResponse response) {
        long total = response.getHits().getTotalHits().value;
        Collection<SearchResult> results = new ArrayList<>();

        for (SearchHit hit : response.getHits().getHits()) {
            SearchResult<FullPublication> searchResult = new SearchResult<>();
            FullPublication fullPublication = buildFullPublication(hit);
            searchResult.setValue(fullPublication);

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
        SearchResponse<FullPublication> scanESRResponse = new SearchResponse(request, total, results);

        // Aggregations vers Histograms pour la Response
        if (response.getAggregations() != null) {
            List<FacetResult> facets = buildFacets(response.getAggregations());
            scanESRResponse.setFacets(facets);
        }

        return scanESRResponse;
    }

    /**
     * Crée des FullPublication à partir d'un SearchHit Elasticsearch
     *
     * @param hit
     * @return
     */
    private FullPublication buildFullPublication(SearchHit hit) {
        try {
            return objectMapper.readValue(hit.getSourceAsString(), FullPublication.class);
        } catch (IOException e) {
            throw new EsSerializationException("Impossible to deserialize from json", e);
        }
    }

    @Override
    protected BoostedSearchFieldsMapperInterface getBoostedSearchFieldsMapper() {
        return new PublicationBoostedSearchFieldsMapper();
    }

    @Override
    protected IIdentifiable getRelatedModel() {
        return new FullPublication();
    }

    @Override
    public void setAggregationService() {
        this.aggregationService = new PublicationAggregationService(getRelatedModel(), lang);
    }

    @Override
    protected void setDefaultSourceFields() {
        List<String> defaultSourceFields = new ArrayList<>();
        defaultSourceFields.add(FullPublication.FIELD_ID);
        defaultSourceFields.add(FullPublication.FIELD_TITLE);
        defaultSourceFields.add(FullPublication.FIELD_DOMAINS_LABEL);
        defaultSourceFields.add(FullPublication.FIELD_SOURCE_TITLE);
        defaultSourceFields.add(FullPublication.FIELD_AUTHORS_FULLNAME);
        defaultSourceFields.add(FullPublication.FIELD_AUTHORS_ROLE);
        defaultSourceFields.add(FullPublication.FIELD_SUBMISSION_DATE);
        defaultSourceFields.add(FullPublication.FIELD_PUBLICATION_DATE);
        defaultSourceFields.add(FullPublication.FIELD_PRODUCTIONTYPE);
        defaultSourceFields.add(FullPublication.FIELD_IS_OA);
        defaultSourceFields.add(FullPublication.FIELD_IS_INTERNATIONAL);
        defaultSourceFields.add(FullPublication.FIELD_IS_OEB);
        defaultSourceFields.add(FullPublication.FIELD_GRANTED_DATE);

        this.defaultSourceFields = defaultSourceFields;
    }

    protected void setDefaultFields() {
        List<String> defaultFields = new ArrayList<>();
        defaultFields.add(FullPublication.FIELD_TITLE);

        this.defaultFields = defaultFields;
    }

    /**
     * Recherche More like this d'elastic
     * @see "https://www.elastic.co/guide/en/elasticsearch/reference/6.7/query-dsl-mlt-query.html"
     * @param likeRequest
     * @return
     * @throws IOException 
     */
    public LikeResponse<FullPublication> moreLikeThis(LikeRequest likeRequest) throws IOException {

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
            MoreLikeThisQueryBuilder.Item item = new MoreLikeThisQueryBuilder.Item(EsIndexEnum.PUBLICATION.getName(), id);
            likeIds[i] = item;
        }

        MoreLikeThisQueryBuilder moreLikeThisQueryBuilderyBuilder = new MoreLikeThisQueryBuilder(fields, likeRequest.getLikeTexts().toArray(new String[0]), likeIds);
        moreLikeThisQueryBuilderyBuilder = moreLikeThisQueryBuilderyBuilder.minTermFreq(1);

        // Exécution de la recherche ES
        SearchSourceBuilder searchBuilder = new SearchSourceBuilder();
        searchBuilder.query(moreLikeThisQueryBuilderyBuilder);

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
        		.search(new org.elasticsearch.action.search.SearchRequest(EsIndexEnum.PUBLICATION.getName()).source(searchBuilder), RequestOptions.DEFAULT);

        long total = elasticSearchResponse.getHits().getTotalHits().value;
        Collection<SearchResult<FullPublication>> results = new ArrayList<>();

        for (SearchHit hit : elasticSearchResponse.getHits().getHits()) {
            SearchResult<FullPublication> searchResult = new SearchResult<>();
            FullPublication fullPublication = buildFullPublication(hit);
            searchResult.setValue(fullPublication);
            results.add(searchResult);
        }
        LikeResponse<FullPublication> scanESRResponse = new LikeResponse(likeRequest, total, results);

        // Aggregations vers Histograms pour la Response
        if (elasticSearchResponse.getAggregations() != null) {
            List<FacetResult> facets = buildFacets(elasticSearchResponse.getAggregations());
            scanESRResponse.setFacets(facets);
        }

        return scanESRResponse;
    }

    /**
     * Effectue la recherche Elasticsearch à partir des données de la requête et les transforme au format souhaité
     * @param id
     * @param distance
     * @param nb
     * @return
     */
    public List<FullPublication> nearSearch(String id, double distance, int nb) throws IOException {
        FullPublication publication = null;
        List<FullPublication> publications = new LinkedList<>();

        List<GeoPoint> gpsList = new ArrayList<>();
        try {
            publication = elasticsearchService.getEsClient().getServicePublication().get(id);

            if(publication == null) {
                throw new NoSuchElementException();
            }

            if (publication.getAffiliations() != null) {
                for (Structure structure : publication.getAffiliations()){
                    if (structure != null) {
                        Optional<Address> mainAddress = structure.getMainAddressList().stream().findFirst();
                        if (mainAddress.isPresent()) {
                            GeoPoint gps = mainAddress.get().getGps();
                            gpsList.add(gps);
                        }
                    }
                }
            }
        }
        catch (NoSuchElementException e) {
            throw new NoSuchElementException("No publication exist with the id " + id + ".");
        } catch (Exception e) {
            throw new IOException("Cannot get address GPS from publication with id " + id);
        }

        if (!gpsList.isEmpty()) {
            SearchSourceBuilder searchBuilder = buildNearRequestBuilder(gpsList, distance, nb);
            org.elasticsearch.action.search.SearchResponse response = elasticsearchService.getEsClient().getEsRestClient()
            		.search(new org.elasticsearch.action.search.SearchRequest(EsIndexEnum.PUBLICATION.getName()).source(searchBuilder), RequestOptions.DEFAULT);

            response.getHits().forEach(hit -> publications.add(buildFullPublication(hit)));
        }

        return publications;
    }

    /**
     * Create SearchRequestBuilder for near request
     * @param gpsList
     * @param distance
     * @param nb
     * @return
     */
    private SearchSourceBuilder buildNearRequestBuilder(List<GeoPoint> gpsList, double distance, int nb) {

        // Requête Elasticsearch de recherche
        BoolQueryBuilder query = queryBuilderService.buildNearQuery(gpsList, distance, FullPublication.FIELD_AFFILIATIONS + "." + FullStructure.FIELDS.ADDRESS.GPS);

        // Exécution de la recherche ES
        SearchSourceBuilder requestBuilder = new SearchSourceBuilder();
        requestBuilder.query(query);
        requestBuilder.size(nb);
        requestBuilder.fetchSource(defaultSourceFields.toArray(new String[0]), null);

        return requestBuilder;
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
        QueryBuilder isFrenchQuery = QueryBuilders.termQuery(FullPublication.FIELD_AFFILIATIONS + "." + FullStructure.FIELDS.IS_FRENCH, true);
        queries.add(isFrenchQuery);

        // On ne veut que les Structures actives (non supprimées)
        QueryBuilder isOldQuery = QueryBuilders.termQuery(FullPublication.FIELD_AFFILIATIONS + "." + FullStructure.FIELDS.STATUS, FullStructure.FIELDS.STATUS_ACTIVE);
        queries.add(isOldQuery);

        return queries;
    }

    /**
     * Effectue la recherche Elasticsearch à partir des données de la requête et les transforme au format souhaité
     *
     * @param searchRequest
     * @return
     * @throws IOException 
     */
    public SearchResponse<FullPublication> geoSearch(SearchRequest searchRequest) throws IOException {
        SearchSourceBuilder searchBuilder = buildSearchBuilder(searchRequest);
        searchBuilder.fetchSource(FETCH_GEO, null);

        SearchResponse<FullPublication> APIResponse = new SearchResponse(searchRequest);
        Collection<SearchResult<FullPublication>> searchResults = scrollRequest(searchBuilder, searchRequest.getPageSize());
        APIResponse.setResults(searchResults);
        APIResponse.setTotal(searchResults.size());

        return APIResponse;
    }

    /**
     * @param searchRequestBuilder
     * @return
     * @throws IOException 
     */
    private Collection<SearchResult<FullPublication>> scrollRequest(SearchSourceBuilder searchBuilder, int maxResult) throws IOException {
        int scrollSize = MAX_FOR_SCROLL;
        if (maxResult < MAX_FOR_SCROLL) {
            scrollSize = maxResult;
        }

        searchBuilder.from(0).size(scrollSize);
        searchBuilder.trackTotalHits(true);

        Collection<SearchResult<FullPublication>> searchResults = new ArrayList<>();
        org.elasticsearch.action.search.SearchResponse response = elasticsearchService.getEsClient().getEsRestClient().search(
        		new org.elasticsearch.action.search.SearchRequest(EsIndexEnum.PUBLICATION.getName())
	        		.scroll(TimeValue.timeValueMillis(SCROLL_TIMEOUT))
	        		.source(searchBuilder), 
	        	RequestOptions.DEFAULT);
        
        response.getHits().forEach(hit -> {
            searchResults.add(new SearchResult<>(buildFullPublication(hit)));
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
                searchResults.add(new SearchResult<>(buildFullPublication(hit)));
            });
        }
        return searchResults;
    }

}
