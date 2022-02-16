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
import fr.gouv.recherche.scanr.db.model.PublicationPersonAffiliation;
import fr.gouv.recherche.scanr.db.model.full.FullPerson;
import fr.gouv.recherche.scanr.db.model.full.FullStructure;
import fr.gouv.recherche.scanr.search.model2.request.LikeRequest;
import fr.gouv.recherche.scanr.search.model2.request.SearchRequest;
import fr.gouv.recherche.scanr.search.model2.response.FacetResult;
import fr.gouv.recherche.scanr.search.model2.response.LikeResponse;
import fr.gouv.recherche.scanr.search.model2.response.SearchResponse;
import fr.gouv.recherche.scanr.search.model2.response.SearchResult;
import fr.gouv.recherche.scanr.util.BoostedSearchFieldsMapperInterface;
import fr.gouv.recherche.scanr.util.PersonBoostedSearchFieldsMapper;

@Service
public class PersonSearchService extends AbstractSearchService {

    private static final Logger log = LoggerFactory.getLogger(PersonSearchService.class);
    private String lang = SearchRequest.DEFAULT_LANG;

    public static final String[] FETCH_GEO = new String[]{
            FullPerson.FIELD_ID,
            FullPerson.FIELD_FULLNAME,
            FullPerson.FIELD_AFFILIATIONS_STRUCTURE + "." + FullStructure.FIELDS.LABEL,
            FullPerson.FIELD_AFFILIATIONS_STRUCTURE + "." + FullStructure.FIELDS.ADDRESS.GPS
    };

    /**
     * Effectue la recherche Elasticsearch à partir des données de la requête et les transforme au format souhaité
     *
     * @param searchRequest
     * @return
     * @throws IOException 
     */
    public SearchResponse<FullPerson> search(SearchRequest searchRequest) throws IOException {
    	SearchSourceBuilder searchBuilder = buildSearchBuilder(searchRequest);
    	
    	org.elasticsearch.action.search.SearchResponse elasticSearchResponse = elasticsearchService.getEsClient().getEsRestClient()
        		.search(new org.elasticsearch.action.search.SearchRequest(EsIndexEnum.PERSON.getName()).source(searchBuilder), RequestOptions.DEFAULT);

        return buildScanESRSearchResponse(searchRequest, elasticSearchResponse);    	
    }

    /**
     * Effectue la recherche Elasticsearch à partir des données de la requête et les transforme au format souhaité
     *
     * @param searchRequest
     * @return
     * @throws IOException 
     */
    public SearchResponse<FullPerson> searchExport(SearchRequest searchRequest, int searchSizeLimit) throws IOException {
    	SearchSourceBuilder searchBuilder = buildSearchBuilder(searchRequest);
    	searchBuilder.size(searchSizeLimit);

        List<String> sourceFields = new ArrayList<>();
        sourceFields.add(FullPerson.FIELD_ID);
        sourceFields.add(FullPerson.FIELD_EXTERNALIDS_ID);
        sourceFields.add(FullPerson.FIELD_FIRSTNAME);
        sourceFields.add(FullPerson.FIELD_LASTNAME);
        sourceFields.add(FullPerson.FIELD_MAIDEN_NAME);
        sourceFields.add(FullPerson.FIELD_FULLNAME);
        sourceFields.add(FullPerson.FIELD_GENDER);
        sourceFields.add(FullPerson.FIELD_DOMAIN_LABEL);
        sourceFields.add(FullPerson.FIELD_WEBSITE);
        searchBuilder.fetchSource(sourceFields.toArray(new String[0]), null);

        org.elasticsearch.action.search.SearchResponse elasticSearchResponse = elasticsearchService.getEsClient().getEsRestClient()
        		.search(new org.elasticsearch.action.search.SearchRequest(EsIndexEnum.PERSON.getName()).source(searchBuilder), RequestOptions.DEFAULT);

        return buildScanESRSearchResponse(searchRequest, elasticSearchResponse);
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
        
        searchBuilder.query(query)
	        .from(searchRequest.getPage() * searchRequest.getPageSize())
	        .size(searchRequest.getPageSize())
	        .trackTotalHits(true);

        // Sort
        List<SortBuilder<?>> sorts = buildSort(searchRequest.getSort(), searchRequest.getLang(), getRelatedModel());
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

        // Setup highlighting
        List<String> searchFields;
        if (searchRequest.getSearchFields() != null && !searchRequest.getSearchFields().isEmpty()) {
            searchFields = searchRequest.getSearchFields();
        }
        else {
            searchFields = new ArrayList<>(boostedSearchFieldsMapper.getBoostConfiguration().keySet());
        }
        HighlightBuilder highlightBuilder = buildHighlight(searchFields, new FullPerson());
        searchBuilder.highlighter(highlightBuilder);

        // Add aggregations to requestBuilder
        for (AggregationBuilder agg : aggs) {
            searchBuilder.aggregation(agg);
        }

        return searchBuilder;
    }

    /**
     * Génère la réponse à renvoyer dans l'API à partir de la réponse Elasticsearch
     *
     * @param request
     * @param response
     * @return
     */
    protected SearchResponse<FullPerson> buildScanESRSearchResponse(SearchRequest request, org.elasticsearch.action.search.SearchResponse response) {
        long total = response.getHits().getTotalHits().value;
        Collection<SearchResult> results = new ArrayList<>();

        for (SearchHit hit : response.getHits().getHits()) {
            SearchResult<FullPerson> searchResult = new SearchResult<>();
            FullPerson fullPerson = buildFullPerson(hit);
            searchResult.setValue(fullPerson);

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
        SearchResponse<FullPerson> scanESRResponse = new SearchResponse(request, total, results);

        // Aggregations vers Histograms pour la Response
        if (response.getAggregations() != null) {
            List<FacetResult> facets = buildFacets(response.getAggregations());
            scanESRResponse.setFacets(facets);
        }

        return scanESRResponse;
    }

    /**
     * Crée des FullPerson à partir d'un SearchHit Elasticsearch
     *
     * @param hit
     * @return
     */
    private FullPerson buildFullPerson(SearchHit hit) {
        try {
            return objectMapper.readValue(hit.getSourceAsString(), FullPerson.class);
        } catch (IOException e) {
            throw new EsSerializationException("Impossible to deserialize from json", e);
        }
    }

    @Override
    protected BoostedSearchFieldsMapperInterface getBoostedSearchFieldsMapper() {
        return new PersonBoostedSearchFieldsMapper();
    }

    @Override
    protected IIdentifiable getRelatedModel() {
        return new FullPerson();
    }

    @Override
    public void setAggregationService() {
        this.aggregationService = new PersonAggregationService(getRelatedModel(), lang);
    }

    @Override
    protected void setDefaultSourceFields() {
        List<String> defaultSourceFields = new ArrayList<>();
        defaultSourceFields.add(FullPerson.FIELD_ID);
        defaultSourceFields.add(FullPerson.FIELD_EXTERNALIDS_ID);
        defaultSourceFields.add(FullPerson.FIELD_FIRSTNAME);
        defaultSourceFields.add(FullPerson.FIELD_LASTNAME);
        defaultSourceFields.add(FullPerson.FIELD_MAIDEN_NAME);
        defaultSourceFields.add(FullPerson.FIELD_GENDER);
        defaultSourceFields.add(FullPerson.FIELD_AFFILIATIONS_STRUCTURE_LABEL);
        defaultSourceFields.add(FullPerson.FIELD_AFFILIATIONS_STRUCTURE_ADDRESS);
        defaultSourceFields.add(FullPerson.FIELD_DOMAIN_LABEL);

        this.defaultSourceFields = defaultSourceFields;
    }

    protected void setDefaultFields() {
        List<String> defaultFields = new ArrayList<>();
        defaultFields.add(FullPerson.FIELD_FULLNAME);

        this.defaultFields = defaultFields;
    }

    /**
     * Recherche More like this d'elastic
     * @see "https://www.elastic.co/guide/en/elasticsearch/reference/6.7/query-dsl-mlt-query.html"
     * @param likeRequest
     * @return
     * @throws IOException 
     */
    public LikeResponse<FullPerson> moreLikeThis(LikeRequest likeRequest) throws IOException {

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
            MoreLikeThisQueryBuilder.Item item = new MoreLikeThisQueryBuilder.Item(EsIndexEnum.PERSON.getName(), id);
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
        		.search(new org.elasticsearch.action.search.SearchRequest(EsIndexEnum.PERSON.getName()).source(searchBuilder), RequestOptions.DEFAULT);

        long total = elasticSearchResponse.getHits().getTotalHits().value;
        Collection<SearchResult> results = new ArrayList<>();

        for (SearchHit hit : elasticSearchResponse.getHits().getHits()) {
            SearchResult searchResult = new SearchResult<FullPerson>();
            FullPerson fullPerson = buildFullPerson(hit);
            searchResult.setValue(fullPerson);
            results.add(searchResult);
        }
        LikeResponse<FullPerson> scanESRResponse = new LikeResponse(likeRequest, total, results);

        // Aggregations vers Histograms pour la Response
        if (elasticSearchResponse.getAggregations() != null) {
            List<FacetResult> facets = buildFacets(elasticSearchResponse.getAggregations());
            scanESRResponse.setFacets(facets);
        }

        return scanESRResponse;
    }

    /**
     * Effectue la recherche Elasticsearch à partir des données de la requête et les transforme au format souhaité
     * @param id : identifiant du document
     * @param distance
     * @param nb
     * @return
     */
    public List<FullPerson> nearSearch(String id, double distance, int nb) throws IOException {
        FullPerson person = null;
        List<FullPerson> people = new LinkedList<>();

        List<GeoPoint> gpsList = new ArrayList<>();
        try {
            person = elasticsearchService.getEsClient().getServicePerson().get(id);

            if(person == null) {
                throw new NoSuchElementException();
            }

            if (person.getAffiliations() != null) {
                for (PublicationPersonAffiliation affiliation : person.getAffiliations()){
                    if (affiliation.getStructure() != null) {
                        Optional<Address> mainAddress = affiliation.getStructure().getMainAddressList().stream().findFirst();
                        if (mainAddress.isPresent()) {
                            GeoPoint gps = mainAddress.get().getGps();
                            gpsList.add(gps);
                        }
                    }
                }
            }
        }
        catch (NoSuchElementException e) {
            throw new NoSuchElementException("No person exist with the id " + id + ".");
        } catch (Exception e) {
            throw new IOException("Cannot get address GPS from person with id " + id);
        }

        if (!gpsList.isEmpty()) {
            SearchSourceBuilder searchBuilder = buildNearRequestBuilder(gpsList, distance, nb);
            org.elasticsearch.action.search.SearchResponse response = elasticsearchService.getEsClient().getEsRestClient()
            		.search(new org.elasticsearch.action.search.SearchRequest(EsIndexEnum.PERSON.getName()).source(searchBuilder), RequestOptions.DEFAULT);
            
            response.getHits().forEach(hit -> people.add(buildFullPerson(hit)));
        }

        return people;
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
        BoolQueryBuilder query = queryBuilderService.buildNearQuery(gpsList, distance, FullPerson.FIELD_AFFILIATIONS_STRUCTURE_ADDRESS_GPS);

        // Exécution de la recherche ES
        SearchSourceBuilder searchBuilder = new SearchSourceBuilder();
        searchBuilder.query(query);
        searchBuilder.size(nb);
        searchBuilder.fetchSource(defaultSourceFields.toArray(new String[0]), null);

        return searchBuilder;
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
        QueryBuilder isFrenchQuery = QueryBuilders.termQuery(FullPerson.FIELD_AFFILIATIONS_STRUCTURE + "." + FullStructure.FIELDS.IS_FRENCH, true);
        queries.add(isFrenchQuery);

        // On ne veut que les Structures actives (non supprimées)
        QueryBuilder isOldQuery = QueryBuilders.termQuery(FullPerson.FIELD_AFFILIATIONS_STRUCTURE + "." + FullStructure.FIELDS.STATUS, FullStructure.FIELDS.STATUS_ACTIVE);
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
    public SearchResponse<FullPerson> geoSearch(SearchRequest searchRequest) throws IOException {
        SearchSourceBuilder searchBuilder = buildSearchBuilder(searchRequest);
        searchBuilder.fetchSource(FETCH_GEO, null);

        SearchResponse<FullPerson> APIResponse = new SearchResponse(searchRequest);
        Collection<SearchResult<FullPerson>> searchResults = scrollRequest(searchBuilder, searchRequest.getPageSize());
        APIResponse.setResults(searchResults);
        APIResponse.setTotal(searchResults.size());

        return APIResponse;
    }

    /**
     * @param searchRequestBuilder
     * @return
     * @throws IOException 
     */
    private Collection<SearchResult<FullPerson>> scrollRequest(SearchSourceBuilder searchBuilder, int maxResult) throws IOException {
        int scrollSize = MAX_FOR_SCROLL;
        if (maxResult < MAX_FOR_SCROLL) {
            scrollSize = maxResult;
        }

        searchBuilder.from(0).size(scrollSize);
        searchBuilder.trackTotalHits(true);

        Collection<SearchResult<FullPerson>> searchResults = new ArrayList<>();
        org.elasticsearch.action.search.SearchResponse response = elasticsearchService.getEsClient().getEsRestClient().search(
        		new org.elasticsearch.action.search.SearchRequest(EsIndexEnum.PERSON.getName())
	        		.scroll(TimeValue.timeValueMillis(SCROLL_TIMEOUT))
	        		.source(searchBuilder), 
	        	RequestOptions.DEFAULT);
        
        response.getHits().forEach(hit -> {
            searchResults.add(new SearchResult<>(buildFullPerson(hit)));
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
                searchResults.add(new SearchResult<>(buildFullPerson(hit)));
            });
        }
        return searchResults;
    }

}
