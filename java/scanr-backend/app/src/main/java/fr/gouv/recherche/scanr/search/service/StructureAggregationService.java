/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.search.service;

import com.sword.utils.elasticsearch.intf.IIdentifiable;
import fr.gouv.recherche.scanr.db.model.full.FullStructure;
import fr.gouv.recherche.scanr.search.model2.response.FacetEnum;
import fr.gouv.recherche.scanr.util.ScanESRReflectionUtils;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
public class StructureAggregationService extends AggregationService {

    // Aggregations fields
    public static final String AGGREGATION_FIELD_BADGES = FullStructure.FIELDS.BADGES.TITLE + "." + FullStructure.FIELDS.LABEL;
    public static final String AGGREGATION_FIELD_INSTITUTIONS = FullStructure.FIELDS.INSTITUTIONS.TITLE + "." + FullStructure.FIELDS.LABEL;
    public static final String AGGREGATION_FIELD_KIND = FullStructure.FIELDS.KIND;
    public static final String AGGREGATION_FIELD_LEVEL = FullStructure.FIELDS.LEVEL;
    public static final String AGGREGATION_FIELD_LOCALISATION = FullStructure.FIELDS.ADDRESS.LOCALISATIONS;
    public static final String AGGREGATION_FIELD_NATURES = FullStructure.FIELDS.NATURE;
    public static final String AGGREGATION_FIELD_PROJECTS_TYPES = FullStructure.FIELDS.PROJECTS.TYPE;
    public static final String AGGREGATION_FIELD_URBAN_HITS = FullStructure.FIELDS.ADDRESS.URBAN_UNIT_LABEL;

    public StructureAggregationService() {
    }

    public StructureAggregationService(IIdentifiable relatedModel, String lang) {
        super(relatedModel, lang);
    }

    @Override
    public List<AggregationBuilder> getPrebuiltAggregations(String lang) {
        this.setLang(lang);

        List<AggregationBuilder> aggregations = new LinkedList<>();
        aggregations.add(getBadgesAggregation());
        aggregations.add(getLevelAggregation());
        aggregations.add(getInstitutionsAggregation());
        aggregations.add(getKindsAggregation());
        aggregations.add(getNaturesAggregation());
        aggregations.add(getProjectsTypesAggregation());
        aggregations.add(getUrbanHitsAggregation());
        aggregations.add(getLocalisationsAggregation());

        return aggregations;
    }

    protected TermsAggregationBuilder getBadgesAggregation() {
        String test = ScanESRReflectionUtils.getESFieldIdentifier(relatedModel, AGGREGATION_FIELD_BADGES, lang);
        return build(FacetEnum.BADGES.getFacetName(), test);
    }

    protected TermsAggregationBuilder getLevelAggregation() {
        return build(FacetEnum.LEVEL.getFacetName(), ScanESRReflectionUtils.getESFieldIdentifier(relatedModel, AGGREGATION_FIELD_LEVEL, lang));
    }

    protected TermsAggregationBuilder getLocalisationsAggregation() {
        return build(FacetEnum.LOCALISATIONS.getFacetName(), ScanESRReflectionUtils.getESFieldIdentifier(relatedModel, AGGREGATION_FIELD_LOCALISATION, lang));
    }

    protected TermsAggregationBuilder getInstitutionsAggregation() {
        return build(FacetEnum.INSTITUTIONS.getFacetName(), ScanESRReflectionUtils.getESFieldIdentifier(relatedModel, AGGREGATION_FIELD_INSTITUTIONS, lang));
    }

    protected TermsAggregationBuilder getKindsAggregation() {
        return build(FacetEnum.KIND.getFacetName(), ScanESRReflectionUtils.getESFieldIdentifier(relatedModel, AGGREGATION_FIELD_KIND, lang));
    }

    protected TermsAggregationBuilder getNaturesAggregation() {
        return build(FacetEnum.NATURES.getFacetName(), ScanESRReflectionUtils.getESFieldIdentifier(relatedModel, AGGREGATION_FIELD_NATURES, lang));
    }

    protected TermsAggregationBuilder getProjectsTypesAggregation() {
        return build(FacetEnum.PROJECTS_TYPES.getFacetName(), ScanESRReflectionUtils.getESFieldIdentifier(relatedModel, AGGREGATION_FIELD_PROJECTS_TYPES, lang));
    }

    protected TermsAggregationBuilder getUrbanHitsAggregation() {
        return build(FacetEnum.URBAN_HITS.getFacetName(), ScanESRReflectionUtils.getESFieldIdentifier(relatedModel, AGGREGATION_FIELD_URBAN_HITS, lang));
    }
}
