/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.api2;

import java.text.ParseException;

import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sword.utils.elasticsearch.exceptions.EsRequestException;
import com.sword.utils.elasticsearch.services.EsServiceGeneral;

import fr.gouv.recherche.scanr.config.elasticsearch.EsIndexEnum;
import fr.gouv.recherche.scanr.db.model.Statistique;
import fr.gouv.recherche.scanr.service.ElasticsearchService;
import fr.gouv.recherche.scanr.util.ElasticsearchDateUtils;

/**
 *
 */
@Controller("countApi")
@RequestMapping("/services/counts")
public class CountApi {

    private static final Logger log = LoggerFactory.getLogger(CountApi.class);

    @Autowired
    private ElasticsearchService elasticsearchService;

    @ResponseBody
    @RequestMapping(value = "", method = RequestMethod.GET)
    public CountReport get() throws EsRequestException {
        EsServiceGeneral esServiceGeneral = elasticsearchService.getEsClient().getServiceGeneral();
        long fullStructures = esServiceGeneral.count(EsIndexEnum.STRUCTURE, QueryBuilders.matchAllQuery());
        long fullProjects = esServiceGeneral.count(EsIndexEnum.PROJECT, QueryBuilders.matchAllQuery());
        long fullPersons = esServiceGeneral.count(EsIndexEnum.PERSON, QueryBuilders.matchAllQuery());
        long fullPublications = esServiceGeneral.count(EsIndexEnum.PUBLICATION, QueryBuilders.matchAllQuery());

        return new CountReport(fullStructures, fullProjects, fullPersons, fullPublications);
    }

    public static class CountReport {
        public long fullStructures;
        public long fullProjects;
        public long fullPersons;
        public long fullPublications;

        public CountReport(long fullStructures, long fullProjects, long fullPersons, long fullPublications) {
            this.fullStructures = fullStructures;
            this.fullProjects = fullProjects;
            this.fullPersons = fullPersons;
            this.fullPublications = fullPublications;
        }
    }

    /**
     * Generic method to return {@code Statistique} for a given index type
     * @param indexType
     * @param startDateFieldName
     * @param startDate
     * @param endDateFieldName
     * @param endDate
     * @return
     * @throws EsRequestException 
     */
    public static  Statistique statistique(ElasticsearchService elasticsearchService, EsIndexEnum index, String startDateFieldName, String startDate, String endDateFieldName, String endDate) 
    		throws EsRequestException {
        Statistique statistique = new Statistique();

        try {
            startDate = ElasticsearchDateUtils.getFormattedDateForESFromString(startDate);
            endDate = ElasticsearchDateUtils.getFormattedDateForESFromString(endDate);
        } catch (ParseException e) {
            log.error("Date parsing error for statistics API", e);
        }

        if (startDate != null) {
            RangeQueryBuilder queryCreated = new RangeQueryBuilder(startDateFieldName);
            queryCreated.lte(endDate);
            queryCreated.gte(startDate);
            long nbCreated = elasticsearchService.getEsClient().getServiceGeneral().count(index, queryCreated);
            statistique.setNbCreated(nbCreated);
        }

        if (endDate !=  null) {
            RangeQueryBuilder queryDeleted = new RangeQueryBuilder(endDateFieldName);
            queryDeleted.lte(endDate);
            queryDeleted.gte(startDate);
            long nbDeleted = elasticsearchService.getEsClient().getServiceGeneral().count(index, queryDeleted);
            statistique.setNbDeleted(nbDeleted);
        }

        statistique.setDiffCreationDeleted(statistique.getNbCreated() - statistique.getNbDeleted());

        return statistique;
    }
}
