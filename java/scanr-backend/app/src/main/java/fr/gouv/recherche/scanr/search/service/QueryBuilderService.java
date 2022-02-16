/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.search.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.GeoBoundingBoxQueryBuilder;
import org.elasticsearch.index.query.GeoDistanceQueryBuilder;
import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.springframework.stereotype.Service;

import com.sword.utils.elasticsearch.intf.IIdentifiable;

import fr.gouv.recherche.scanr.api.util.ApiConstants;
import fr.gouv.recherche.scanr.db.model.full.FullStructure;
import fr.gouv.recherche.scanr.search.exceptions.InvalidSearchFilterException;
import fr.gouv.recherche.scanr.search.model2.request.DateRangeFilter;
import fr.gouv.recherche.scanr.search.model2.request.GeoGridFilter;
import fr.gouv.recherche.scanr.search.model2.request.MultiValueSearchFilter;
import fr.gouv.recherche.scanr.search.model2.request.RangeFilter;
import fr.gouv.recherche.scanr.search.model2.request.SearchFilter;
import fr.gouv.recherche.scanr.search.model2.request.SearchRequest;
import fr.gouv.recherche.scanr.util.BoostedSearchFieldsMapperInterface;
import fr.gouv.recherche.scanr.util.ElasticsearchDateUtils;
import fr.gouv.recherche.scanr.util.ScanESRReflectionUtils;

@Service
public class QueryBuilderService {

    /**
     * Construction de la requête de recherche pour Elasticsearch, en fonction de la requête passée.
     *
     * @param searchRequest
     * @return
     */
    public BoolQueryBuilder buildQuery(SearchRequest searchRequest, BoostedSearchFieldsMapperInterface boostedSearchFieldsMapper, IIdentifiable relatedModel) {
        List<QueryBuilder> queries = new LinkedList<>();

        // Fulltext : query on SearchFields
        if (searchRequest.getQuery() != null && !searchRequest.getQuery().isEmpty()) {
            QueryBuilder query = QueryBuilders.simpleQueryStringQuery(searchRequest.getQuery())
                    .fields(getBoostedFields(searchRequest, boostedSearchFieldsMapper, relatedModel))
                    .defaultOperator(Operator.AND);
            queries.add(query);
        }

        // Filters
        if (searchRequest.getFilters() != null && !searchRequest.getFilters().isEmpty()) {
            for (Map.Entry<String, SearchFilter> searchFilterEntry : searchRequest.getFilters().entrySet()) {
                if (searchFilterEntry.getValue() instanceof MultiValueSearchFilter) {
                    queries.add(getMultiValueSearchFilterBoolQuery(searchFilterEntry, null, relatedModel));
                } else if (searchFilterEntry.getValue() instanceof DateRangeFilter) {
                    queries.add(getRangeFilterBoolQuery(searchFilterEntry, Boolean.TRUE, null));
                }else if (searchFilterEntry.getValue() instanceof RangeFilter) {
                    queries.add(getRangeFilterBoolQuery(searchFilterEntry, Boolean.FALSE, null));
                } else if (searchFilterEntry.getValue() instanceof GeoGridFilter) {
                    queries.add(getGeoGridFilterBoolQuery(searchFilterEntry, null));
                }
            }
        }

        // Construction de la query globale
        BoolQueryBuilder query = QueryBuilders.boolQuery();
        for (QueryBuilder subQuery : queries) {
            query.must(subQuery);
        }

        return query;
    }

    /**
     * @param gps
     * @param distance
     * @return
     */
    public BoolQueryBuilder buildNearQuery(GeoPoint gps, double distance) {

        BoolQueryBuilder query = QueryBuilders.boolQuery();
        MatchAllQueryBuilder matchAllQuery = QueryBuilders.matchAllQuery();

        query.must(matchAllQuery);

        GeoDistanceQueryBuilder geoDistanceQuery = QueryBuilders.geoDistanceQuery(FullStructure.FIELDS.ADDRESS.GPS);
        geoDistanceQuery.distance(distance, DistanceUnit.KILOMETERS);
        geoDistanceQuery.point(gps);

        query.filter(geoDistanceQuery);

        return query;
    }

    /**
     * @param gpsList
     * @param distance
     * @return
     */
    public BoolQueryBuilder buildNearQuery(List<GeoPoint> gpsList, double distance, String fieldName) {

        BoolQueryBuilder query = QueryBuilders.boolQuery();
        MatchAllQueryBuilder matchAllQuery = QueryBuilders.matchAllQuery();

        query.must(matchAllQuery);

        for (GeoPoint gps : gpsList) {
            GeoDistanceQueryBuilder geoDistanceQuery = QueryBuilders.geoDistanceQuery(fieldName);
            geoDistanceQuery.distance(distance, DistanceUnit.KILOMETERS);
            geoDistanceQuery.point(gps);

            query.filter(geoDistanceQuery);
        }

        return query;
    }

    /**
     * Récupération des boosts à appliquer pour les champs donnés
     * @param searchRequest
     * @param boostedSearchFieldsMapper
     * @param relatedModel
     * @return
     */
    private Map<String, Float> getBoostedFields(SearchRequest searchRequest, BoostedSearchFieldsMapperInterface boostedSearchFieldsMapper, IIdentifiable relatedModel) {
        Map<String, Float> boostedFields;

        List<String> fields = searchRequest.getSearchFields();

        // Si aucun champ fourni, on prend la configuration complète
        if (fields.isEmpty()) {
            boostedFields = boostedSearchFieldsMapper.getBoostConfiguration();

            for (HashMap.Entry<String, Float> field : boostedSearchFieldsMapper.getBoostConfiguration().entrySet()) {
                // Cas particulier pour les champs traduits
                if (ScanESRReflectionUtils.isFieldTranslatable(relatedModel, field.getKey())) {
                    boostedFields.put(field.getKey() + ".*", field.getValue());
                    boostedFields.remove(field.getKey());
                }
            }
        } else {
            boostedFields = fields.stream().collect(Collectors.toMap(item -> item, boostedSearchFieldsMapper::getBoostForField));

            // Cas particulier pour les champs traduits
            for (String field : fields) {
                if (ScanESRReflectionUtils.isFieldTranslatable(relatedModel, field)) {
                    boostedFields.put(field + ".*", boostedFields.get(field));
                    boostedFields.remove(field);
                }
            }
        }
        return boostedFields;
    }

    public List<String> getFieldsWithLanguage(List<String> fields, String lang, IIdentifiable relatedModel) {
        if (relatedModel == null) {
            return fields;
        }

        String langSearched = lang;
        if (StringUtils.isEmpty(langSearched)) {
            langSearched = ApiConstants.DEFAULT_LANGUAGE;
        }

        List<String> fieldsWithLanguage = new ArrayList<>();

        for (String field : fields) {
            if (ScanESRReflectionUtils.isFieldTranslatable(relatedModel, field)) {
                fieldsWithLanguage.add(field + "." + langSearched);
            } else {
                fieldsWithLanguage.add(field);
            }
        }

        return fieldsWithLanguage;
    }

    /**
     * Retourne la boolQuery construite à partir d'un RangeFilter
     *
     * @param filter
     * @return
     */
    public static QueryBuilder getRangeFilterBoolQuery(Map.Entry<String, SearchFilter> filter, Boolean isDate, String forcedField) {
        isDate = isDate == null ? Boolean.FALSE : isDate;
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        RangeFilter searchFilter = (RangeFilter) filter.getValue();
        String field = (forcedField != null && !forcedField.isEmpty()) ? forcedField : filter.getKey();

        if (searchFilter.min != null || searchFilter.max != null) {
            RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery(field);
            if (searchFilter.min != null) {
                if (isDate) {
                    rangeQuery.gte(ElasticsearchDateUtils.getFormattedDateForES((Date) searchFilter.min));
                }
                else {
                    rangeQuery.gte(searchFilter.min);
                }
            }
            if (searchFilter.max != null) {
                if (isDate) {
                    rangeQuery.lte(ElasticsearchDateUtils.getFormattedDateForES((Date) searchFilter.max));
                }
                else {
                    rangeQuery.lt(searchFilter.max);
                }
            }

            if (searchFilter.missing) {
                boolQuery.should(rangeQuery);
            } else {
                return rangeQuery;
            }
        }

        return boolQuery;
    }

    /**
     * Retourne la boolQuery construite à partir d'un GeoGridFilter
     *
     * @param filter
     * @return
     */
    public static QueryBuilder getGeoGridFilterBoolQuery(Map.Entry<String, SearchFilter> filter, String forcedField) {
        GeoGridFilter searchFilter = (GeoGridFilter) filter.getValue();
        String field = (forcedField != null && !forcedField.isEmpty()) ? forcedField : filter.getKey();

        if (field == null || searchFilter.topLeft == null || searchFilter.bottomRight == null) {
            throw new InvalidSearchFilterException(searchFilter, "Empty field name or value");
        }

        GeoBoundingBoxQueryBuilder geoBoundingBoxQuery = QueryBuilders.geoBoundingBoxQuery(field);
        geoBoundingBoxQuery.setCorners(searchFilter.topLeft, searchFilter.bottomRight);

        return geoBoundingBoxQuery;
    }

    /**
     * Retourne la boolQuery construite à partir d'un MultiValueSearchFilter
     * @param filter
     * @param forcedField
     * @return
     */
    public static BoolQueryBuilder getMultiValueSearchFilterBoolQuery(Map.Entry<String, SearchFilter> filter, String forcedField, IIdentifiable relatedModel) {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        BoolQueryBuilder subBoolQuery = QueryBuilders.boolQuery();
        MultiValueSearchFilter searchFilter = (MultiValueSearchFilter) filter.getValue();

        String field = (forcedField != null && !forcedField.isEmpty()) ? forcedField : filter.getKey();
        if (!ScanESRReflectionUtils.isFieldNumberDateOrBoolean(relatedModel, field)) {
            field += FullStructure.KEYWORD_SUFFIXE;
        }
        MultiValueSearchFilter.Operator operator = searchFilter.getOp();
        List<String> values = searchFilter.getValues();

        if (values.size() <= 0 && operator != MultiValueSearchFilter.Operator.exists) {
            throw new InvalidSearchFilterException(searchFilter, "Empty values field");
        }

        // all, any, none, not_all, exists
        String finalField = field;
        switch (operator) {
            case all:
                values.forEach(el -> boolQuery.must(QueryBuilders.termQuery(finalField, el)));
                break;
            case any:
                values.forEach(el -> boolQuery.should(QueryBuilders.termQuery(finalField, el)));
                boolQuery.minimumShouldMatch(1);
                break;
            case not_all:
                values.forEach(el -> subBoolQuery.should(QueryBuilders.termQuery(finalField, el)));
                boolQuery.mustNot(subBoolQuery);
                break;
            case none:
                values.forEach(el -> subBoolQuery.must(QueryBuilders.termQuery(finalField, el)));
                boolQuery.mustNot(subBoolQuery);
                break;
            case exists:            	
                boolQuery.must( QueryBuilders.existsQuery(finalField));
                break;
        }

        return boolQuery;
    }

}
