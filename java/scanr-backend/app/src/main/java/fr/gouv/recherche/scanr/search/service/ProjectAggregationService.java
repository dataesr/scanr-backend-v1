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
public class ProjectAggregationService extends AggregationService {

    public static final String AGGREGATION_FIELD_TYPE = "type";
    public static final String AGGREGATION_FIELD_ACTION_LABEL = "action.label";
    public static final String AGGREGATION_FIELD_CALL_LABEL = "call.label";
    public static final String AGGREGATION_FIELD_BADGES = "badges.label";
    public static final String AGGREGATION_FIELD_START_DATE = "startDate";
    public static final String AGGREGATION_FIELD_END_DATE = "endDate";
    public static final String AGGREGATION_FIELD_BUDGET_FINANCED = "budgetFinanced";
    public static final String AGGREGATION_FIELD_BUDGET_TOTAL = "budgetTotal";
    public static final String AGGREGATION_FIELD_LOCALISATION = "participants.structure.address.localisationSuggestions";
    public static final String AGGREGATION_FIELD_YEAR = "year";

    public ProjectAggregationService() {
    }

    public ProjectAggregationService(IIdentifiable relatedModel, String lang) {
        super(relatedModel, lang);
    }

    @Override
    public List<AggregationBuilder> getPrebuiltAggregations(String lang) {
        this.setLang(lang);

        List<AggregationBuilder> aggregations = new LinkedList<>();
        aggregations.add(getTypesAggregation());
        aggregations.add(getActionLabelsAggregation());
        aggregations.add(getCallLabelsAggregation());
        aggregations.add(getBadgesAggregation());
        aggregations.add(getStartDatesAggregation());
        aggregations.add(getEndDatesAggregation());
        aggregations.add(getBudgetFinancedAggregation());
        aggregations.add(getBudgetTotalAggregation());
        aggregations.add(getLocalisationsAggregation());
        aggregations.add(getYearsAggregation());

        return aggregations;
    }

    protected TermsAggregationBuilder getTypesAggregation() {
        return build(FacetEnum.PROJECTS_TYPES.getFacetName(), ScanESRReflectionUtils.getESFieldIdentifier(relatedModel, AGGREGATION_FIELD_TYPE, lang));
    }

    protected TermsAggregationBuilder getActionLabelsAggregation() {
        return build(FacetEnum.ACTION_LABELS.getFacetName(), ScanESRReflectionUtils.getESFieldIdentifier(relatedModel, AGGREGATION_FIELD_ACTION_LABEL, lang));
    }

    protected TermsAggregationBuilder getCallLabelsAggregation() {
        return build(FacetEnum.CALL_LABELS.getFacetName(), ScanESRReflectionUtils.getESFieldIdentifier(relatedModel, AGGREGATION_FIELD_CALL_LABEL, lang));
    }

    protected TermsAggregationBuilder getBadgesAggregation() {
        return build(FacetEnum.BADGES.getFacetName(), ScanESRReflectionUtils.getESFieldIdentifier(relatedModel, AGGREGATION_FIELD_BADGES, lang));
    }

    protected TermsAggregationBuilder getStartDatesAggregation() {
        return build(FacetEnum.START_DATES.getFacetName(), ScanESRReflectionUtils.getESFieldIdentifier(relatedModel, AGGREGATION_FIELD_START_DATE, lang));
    }

    protected TermsAggregationBuilder getEndDatesAggregation() {
        return build(FacetEnum.END_DATES.getFacetName(), ScanESRReflectionUtils.getESFieldIdentifier(relatedModel, AGGREGATION_FIELD_END_DATE, lang));
    }

    protected TermsAggregationBuilder getBudgetFinancedAggregation() {
        return build(FacetEnum.BUDGET_FINANCED.getFacetName(), ScanESRReflectionUtils.getESFieldIdentifier(relatedModel, AGGREGATION_FIELD_BUDGET_FINANCED, lang));
    }

    protected TermsAggregationBuilder getBudgetTotalAggregation() {
        return build(FacetEnum.BUDGET_TOTAL.getFacetName(), ScanESRReflectionUtils.getESFieldIdentifier(relatedModel, AGGREGATION_FIELD_BUDGET_TOTAL, lang));
    }

    protected TermsAggregationBuilder getLocalisationsAggregation() {
        return build(FacetEnum.LOCALISATIONS.getFacetName(), ScanESRReflectionUtils.getESFieldIdentifier(relatedModel, AGGREGATION_FIELD_LOCALISATION, lang));
    }

    protected TermsAggregationBuilder getYearsAggregation() {
        return build(FacetEnum.YEARS.getFacetName(), ScanESRReflectionUtils.getESFieldIdentifier(relatedModel, AGGREGATION_FIELD_YEAR, lang));
    }

}
