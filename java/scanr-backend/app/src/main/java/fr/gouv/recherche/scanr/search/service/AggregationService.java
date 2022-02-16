/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.search.service;

import com.sword.utils.elasticsearch.intf.IIdentifiable;
import fr.gouv.recherche.scanr.search.model2.request.*;
import fr.gouv.recherche.scanr.util.BoostedSearchFieldsMapperInterface;
import fr.gouv.recherche.scanr.util.ScanESRReflectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.aggregations.bucket.filter.FiltersAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.filter.FiltersAggregator;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.*;

@Component
public class AggregationService {

    public static final Integer DEFAULT_SIZE = 20;
    public static final Integer DEFAULT_MIN_DOC_COUNT = 1;

    protected IIdentifiable relatedModel;
    protected String lang;

    public AggregationService() {
    }

    public AggregationService(IIdentifiable relatedModel, String lang) {
        this.relatedModel = relatedModel;
        this.lang = lang;
    }

    public TermsAggregationBuilder build(String name, String field) {
        return build(name, field, DEFAULT_SIZE, DEFAULT_MIN_DOC_COUNT);
    }

    public TermsAggregationBuilder build(String name, String field, Integer size, Integer minDocCount) {
        return AggregationBuilders.terms(name)
                .field(field)
                .size(size)
                .minDocCount(minDocCount);
    }

    public FiltersAggregationBuilder buildFilters(String name, FiltersAggregator.KeyedFilter[] filters) {
        return AggregationBuilders.filters(name, filters);
    }

    /**
     * Construction des aggregations pour les facettes
     *
     * @param searchRequest
     * @return List<AggregationBuilder>
     */
    public List<AggregationBuilder> buildAggregations(SearchRequest searchRequest, BoostedSearchFieldsMapperInterface boostedSearchFieldsMapper, IIdentifiable relatedModel, String lang) {
        List<AggregationBuilder> aggregations = new LinkedList<>();

        if (searchRequest.getAggregations() == null) {
            return aggregations;
        }

        for (Map.Entry<String, Aggregation> aggregationEntry : searchRequest.getAggregations().entrySet()) {
            Aggregation aggregation = aggregationEntry.getValue();
            AggregationBuilder aggregationBuilder = null;

            if (aggregation.getFilters() != null && !aggregation.getFilters().isEmpty()) {
                aggregationBuilder = getFiltersAggregationBuilder(aggregationEntry, relatedModel);
            }
            else {
                aggregationBuilder = getAggregationBuilder(aggregationEntry);
            }

            aggregations.add(aggregationBuilder);
        }

        return aggregations;
    }

    /**
     * Build TermsAggregationBuilder for Aggregation entry
     * @param aggregationEntry
     * @return
     */
    private AggregationBuilder getAggregationBuilder(Map.Entry<String, Aggregation> aggregationEntry) {

        String aggregationName = aggregationEntry.getKey();
        Aggregation aggregation = aggregationEntry.getValue();
        String aggregationField = aggregation.getField();

        aggregationField = ScanESRReflectionUtils.getESFieldIdentifier(relatedModel, aggregationField, lang);

        TermsAggregationBuilder aggregationBuilder = build(aggregationName, aggregationField, aggregation.getSize(), aggregation.getMin_doc_count());

        BucketOrder aggregationTermsOrder = getAggregationTermsOrder(aggregation.getOrder());
        if (aggregationTermsOrder != null) {
            aggregationBuilder.order(aggregationTermsOrder);
        }

        return aggregationBuilder;
    }


    /**
     * Build FiltersAggregationBuilder for Aggregation entry
     * @param aggregationEntry
     * @param relatedModel
     * @return
     */
    private AggregationBuilder getFiltersAggregationBuilder(Map.Entry<String, Aggregation> aggregationEntry, IIdentifiable relatedModel) {

        String aggregationField = aggregationEntry.getKey();
        Aggregation aggregation = aggregationEntry.getValue();

        Collection<FiltersAggregator.KeyedFilter> keyedFilters = new ArrayList<>();

        for (Map.Entry<String, SearchFilter> aggregationFilterEntry : aggregation.getFilters().entrySet()) {
            QueryBuilder queryBuilder = null;

            if (aggregationFilterEntry.getValue() instanceof MultiValueSearchFilter) {
                queryBuilder = QueryBuilderService.getMultiValueSearchFilterBoolQuery(aggregationFilterEntry, aggregationField, relatedModel);
            } else if (aggregationFilterEntry.getValue() instanceof DateRangeFilter) {
                queryBuilder = QueryBuilderService.getRangeFilterBoolQuery(aggregationFilterEntry, Boolean.TRUE, aggregationField);
            }else if (aggregationFilterEntry.getValue() instanceof RangeFilter) {
                queryBuilder = QueryBuilderService.getRangeFilterBoolQuery(aggregationFilterEntry, Boolean.FALSE, aggregationField);
            } else if (aggregationFilterEntry.getValue() instanceof GeoGridFilter) {
                queryBuilder = QueryBuilderService.getGeoGridFilterBoolQuery(aggregationFilterEntry, aggregationField);
            }

            if (queryBuilder != null) {
                FiltersAggregator.KeyedFilter keyedFilter = new FiltersAggregator.KeyedFilter(aggregationFilterEntry.getKey(), queryBuilder);
                keyedFilters.add(keyedFilter);
            }
        };

        return buildFilters(
                aggregationField,
                keyedFilters.toArray(new FiltersAggregator.KeyedFilter[keyedFilters.size()])
        );
    }

    /**
     * Retourne l'objet de tri pour les TermsAggregation
     *
     * @param order
     * @return
     */
    private BucketOrder getAggregationTermsOrder(AggregationTermsOrder order) {
        BucketOrder bucketOrder = null;
        switch (order.getType()) {
            case COUNT:
                bucketOrder = BucketOrder.count(order.getDirection().toBool());
                break;
            case KEY:
                bucketOrder = BucketOrder.key(order.getDirection().toBool());
                break;
        }
        return bucketOrder;
    }

    /**
     * Retourne les aggregations définies comme devant être générées par défaut
     * @param lang
     * @return
     */
    public List<AggregationBuilder> getPrebuiltAggregations(String lang) {
        this.setLang(lang);
        return new LinkedList<>();
    }

    public void setLang(String lang) {
        this.lang = lang;
    }
}
