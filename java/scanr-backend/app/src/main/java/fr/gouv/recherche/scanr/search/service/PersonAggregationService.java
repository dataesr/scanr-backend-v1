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
public class PersonAggregationService extends AggregationService {

    public static final String AGGREGATION_FIELD_AWARD = "awards.label";
    public static final String AGGREGATION_FIELD_BADGE = "badges.label";
    public static final String AGGREGATION_FIELD_AFFILIATIONS_STRUCTURE_LABEL = "affiliations.structure.label";
    public static final String AGGREGATION_FIELD_LOCALISATION = "affiliations.structure.address.localisationSuggestions";

    public PersonAggregationService() {
    }

    public PersonAggregationService(IIdentifiable relatedModel, String lang) {
        super(relatedModel, lang);
    }

    @Override
    public List<AggregationBuilder> getPrebuiltAggregations(String lang) {
        this.setLang(lang);

        List<AggregationBuilder> aggregations = new LinkedList<>();
        aggregations.add(getAwardAggregation());
        aggregations.add(getAffiliationStructureLabelAggregation());
        aggregations.add(getBadgesAggregation());
        aggregations.add(getLocalisationsAggregation());

        return aggregations;
    }

    protected TermsAggregationBuilder getAwardAggregation() {
        return build(FacetEnum.AWARDS.getFacetName(), ScanESRReflectionUtils.getESFieldIdentifier(relatedModel, AGGREGATION_FIELD_AWARD, lang));
    }

    protected TermsAggregationBuilder getAffiliationStructureLabelAggregation() {
        return build(FacetEnum.AFFILIATION_STRUCTURE_LABEL.getFacetName(), ScanESRReflectionUtils.getESFieldIdentifier(relatedModel, AGGREGATION_FIELD_AFFILIATIONS_STRUCTURE_LABEL, lang));
    }

    protected TermsAggregationBuilder getBadgesAggregation() {
        return build(FacetEnum.BADGES.getFacetName(), ScanESRReflectionUtils.getESFieldIdentifier(relatedModel, AGGREGATION_FIELD_BADGE, lang));
    }

    protected TermsAggregationBuilder getLocalisationsAggregation() {
        return build(FacetEnum.LOCALISATIONS.getFacetName(), ScanESRReflectionUtils.getESFieldIdentifier(relatedModel, AGGREGATION_FIELD_LOCALISATION, lang));
    }

}
