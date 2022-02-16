/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.search.service;

import fr.gouv.recherche.scanr.search.model2.response.FacetEnum;
import fr.gouv.recherche.scanr.search.model2.response.FacetResult;
import fr.gouv.recherche.scanr.search.model2.response.FacetResultEntry;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.MultiBucketsAggregation;
import org.elasticsearch.search.aggregations.bucket.MultiBucketsAggregation.Bucket;
import org.elasticsearch.search.aggregations.bucket.SingleBucketAggregation;
import org.elasticsearch.search.aggregations.bucket.filter.InternalFilter;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Service
public class FacetService {

    /**
     * Construit les resultats de facette à partir des {@link Aggregation} retournées par ES
     */
    public List<FacetResult> buildFacetResults(Aggregations aggregations) {

        // On construit la liste des facettes (aggregations)
        List<FacetResult> facets = new LinkedList<>();

        for (Map.Entry<String, Aggregation> aggregationEntry : aggregations.getAsMap().entrySet()) {

            String facetId = aggregationEntry.getKey();
            if (FacetEnum.GLOBAL_FACET_ID.equals(facetId)) {
                for (Aggregation filterAgg : ((SingleBucketAggregation) aggregationEntry.getValue()).getAggregations()) {
                    for (Aggregation agg : ((SingleBucketAggregation) filterAgg).getAggregations()) {
                        FacetResult facet = buildFacetResult(agg, agg.getName());
                        facets.add(facet);
                    }
                }
            } else {
                FacetResult facet = buildFacetResult(aggregationEntry.getValue(), facetId);
                facets.add(facet);
            }
        }

        return facets;
    }

    private FacetResult buildFacetResult(Aggregation aggregation, String facetId) {
        FacetEnum facetType = null;

        try {
            facetType = FacetEnum.valueFromFacetName(facetId);
        } catch (Exception e) {
            //
        }

        FacetResult facet = new FacetResult();
        facet.setId(facetId);
        facet.setEntries(new LinkedList<>());

        if (aggregation instanceof SingleBucketAggregation) {
            for (Aggregation agg : ((SingleBucketAggregation) aggregation).getAggregations()) {
                if (agg instanceof MultiBucketsAggregation) {
                    addFacetEntries(((MultiBucketsAggregation) agg).getBuckets(), facet, facetType);
                } else if (agg instanceof InternalFilter) {
                    for (Aggregation agg2 : ((InternalFilter) agg).getAggregations()) {
                        if (agg2 instanceof MultiBucketsAggregation) {
                            addFacetEntries(((MultiBucketsAggregation) agg2).getBuckets(), facet, facetType);
                        } else {
                            throw new RuntimeException("Unimplemented facet sub-aggregation : " + agg2.getClass());
                        }
                    }
                } else {
                    throw new RuntimeException("Unimplemented facet sub-aggregation : " + agg.getClass());
                }
            }
        } else if (aggregation instanceof MultiBucketsAggregation) {
            addFacetEntries(((MultiBucketsAggregation) aggregation).getBuckets(), facet, facetType);
        } else {
            throw new RuntimeException("Unimplemented facet type aggregation : " + aggregation.getClass());
        }
        return facet;
    }

    private void addFacetEntries(Collection<? extends Bucket> buckets, FacetResult facet, FacetEnum facetType) {
        for (Bucket bucket : buckets) {
            FacetResultEntry facetEntry = new FacetResultEntry();
            facetEntry.setCount(bucket.getDocCount());
            // facetType = null dans le cas de facettes custom
            String facetEntryValue = facetType == null ? bucket.getKeyAsString() : facetType.bucketKeyAsString(bucket);
            facetEntry.setValue(facetEntryValue);

            for (Aggregation agg : bucket.getAggregations()) {
                FacetResult subFacet = buildFacetResult(agg, agg.getName());
                facetEntry.addSubFacet(subFacet);
            }

            facet.getEntries().add(facetEntry);
        }
    }
}
