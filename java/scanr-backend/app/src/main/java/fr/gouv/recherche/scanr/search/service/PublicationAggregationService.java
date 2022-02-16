/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */

package fr.gouv.recherche.scanr.search.service;

import com.sword.utils.elasticsearch.intf.IIdentifiable;
import fr.gouv.recherche.scanr.search.model2.response.FacetEnum;
import fr.gouv.recherche.scanr.util.ScanESRReflectionUtils;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
public class PublicationAggregationService extends AggregationService {

    public static final String AGGREGATION_FIELD_SOURCE_TITLE = "source.title";
    public static final String AGGREGATION_FIELD_SOURCE_PUBLISHER = "source.publisher";
    public static final String AGGREGATION_FIELD_PUBLICATION_DATE = "publicationDate";
    public static final String AGGREGATION_FIELD_SUBMISSION_DATE = "submissionDate";
    public static final String AGGREGATION_FIELD_TYPE = "type";
    public static final String AGGREGATION_FIELD_PRODUCTIONTYPE = "productionType";
    public static final String AGGREGATION_FIELD_IS_OA = "isOa";
    public static final String AGGREGATION_FIELD_AFFILIATION_ID = "affiliations.id";
    public static final String AGGREGATION_FIELD_AFFILIATION_LABEL = "affiliations.label";

    public PublicationAggregationService() {
    }

    public PublicationAggregationService(IIdentifiable relatedModel, String lang) {
        super(relatedModel, lang);
    }

    @Override
    public List<AggregationBuilder> getPrebuiltAggregations(String lang) {
        this.setLang(lang);

        List<AggregationBuilder> aggregations = new LinkedList<>();
        aggregations.add(getSourceTitleAggregation());
        aggregations.add(getSourcePublisherAggregation());
        aggregations.add(getPublicationDateAggregation());
        aggregations.add(getSubmissionDateAggregation());
        aggregations.add(getTypesAggregation());
        aggregations.add(getProductionTypesAggregation());
        aggregations.add(getIsOaAggregation());
        aggregations.add(getAffiliationIDsAggregation());
        aggregations.add(getAffiliationLabelsAggregation());

        return aggregations;
    }

    protected TermsAggregationBuilder getSourceTitleAggregation() {
        return build(FacetEnum.SOURCE_TITLE.getFacetName(), ScanESRReflectionUtils.getESFieldIdentifier(relatedModel, AGGREGATION_FIELD_SOURCE_TITLE, lang));
    }

    protected TermsAggregationBuilder getSourcePublisherAggregation() {
        return build(FacetEnum.SOURCE_PUBLISHER.getFacetName(), ScanESRReflectionUtils.getESFieldIdentifier(relatedModel, AGGREGATION_FIELD_SOURCE_PUBLISHER, lang));
    }

    protected TermsAggregationBuilder getPublicationDateAggregation() {
        return build(FacetEnum.PUBLICATION_DATE.getFacetName(), ScanESRReflectionUtils.getESFieldIdentifier(relatedModel, AGGREGATION_FIELD_PUBLICATION_DATE, lang));
    }

    protected TermsAggregationBuilder getSubmissionDateAggregation() {
        return build(FacetEnum.SUBMISSION_DATE.getFacetName(), ScanESRReflectionUtils.getESFieldIdentifier(relatedModel, AGGREGATION_FIELD_SUBMISSION_DATE, lang));
    }

    protected TermsAggregationBuilder getTypesAggregation() {
        return build(FacetEnum.PUBLICATION_TYPES.getFacetName(), ScanESRReflectionUtils.getESFieldIdentifier(relatedModel, AGGREGATION_FIELD_TYPE, lang));
    }

    protected TermsAggregationBuilder getProductionTypesAggregation() {
        return build(FacetEnum.PRODUCTION_TYPES.getFacetName(), ScanESRReflectionUtils.getESFieldIdentifier(relatedModel, AGGREGATION_FIELD_PRODUCTIONTYPE, lang));
    }

    protected TermsAggregationBuilder getIsOaAggregation() {
        return build(FacetEnum.IS_OA.getFacetName(), ScanESRReflectionUtils.getESFieldIdentifier(relatedModel, AGGREGATION_FIELD_IS_OA, lang));
    }

    protected TermsAggregationBuilder getAffiliationIDsAggregation() {
        return build(FacetEnum.AFFILIATION_STRUCTURE_ID.getFacetName(), ScanESRReflectionUtils.getESFieldIdentifier(relatedModel, AGGREGATION_FIELD_AFFILIATION_ID, lang));
    }


    protected TermsAggregationBuilder getAffiliationLabelsAggregation() {
        return build(FacetEnum.AFFILIATION_STRUCTURE_LABEL.getFacetName(), ScanESRReflectionUtils.getESFieldIdentifier(relatedModel, AGGREGATION_FIELD_AFFILIATION_LABEL, lang));
    }


}
