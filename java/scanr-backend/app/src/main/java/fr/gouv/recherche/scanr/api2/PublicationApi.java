/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */

package fr.gouv.recherche.scanr.api2;

import static fr.gouv.recherche.scanr.api.util.ApiConstants.PRODUCES_JSON;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sword.utils.elasticsearch.exceptions.EsRequestException;

import fr.gouv.recherche.scanr.api.util.ApiConstants;
import fr.gouv.recherche.scanr.api.util.ExportUtil;
import fr.gouv.recherche.scanr.api.util.UserLocale;
import fr.gouv.recherche.scanr.config.elasticsearch.EsIndexEnum;
import fr.gouv.recherche.scanr.db.model.Statistique;
import fr.gouv.recherche.scanr.db.model.full.FullPublication;
import fr.gouv.recherche.scanr.search.model2.request.LikeRequest;
import fr.gouv.recherche.scanr.search.model2.request.SearchRequest;
import fr.gouv.recherche.scanr.search.model2.response.LikeResponse;
import fr.gouv.recherche.scanr.search.model2.response.SearchResponse;
import fr.gouv.recherche.scanr.search.service.PublicationSearchService;
import fr.gouv.recherche.scanr.service.ElasticsearchService;
import fr.gouv.recherche.scanr.util.ExcelExport;
import io.swagger.annotations.ApiOperation;

@Controller("publicationApi2")
@RequestMapping("/v2/publications/")
public class PublicationApi {

    private static final String ESR_CREATION_FIELD_NAME = "createdAt";
    private static final String ESR_DELETION_FIELD_NAME = "removedAt";

    @Autowired
    private PublicationSearchService publicationSearchService;

    @Autowired
    private ElasticsearchService elasticsearchService;

    @ApiOperation("Gets by id in Elasticsearch")
    @ResponseBody
    @RequestMapping(value = "/{id:.+}", method = RequestMethod.GET, produces = PRODUCES_JSON)
    public FullPublication getPublication(@UserLocale List<Locale> locales, @PathVariable String id) throws EsRequestException {
        try {
            // ID may contains '/', they are encoded for avoiding API conflicts
            String decodedID = URLDecoder.decode(id, StandardCharsets.UTF_8.name());
            return elasticsearchService.getEsClient().getServicePublication().get(decodedID);
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    @ResponseBody
    @RequestMapping(value = "/statistics", method = RequestMethod.POST, produces = PRODUCES_JSON)
    public Statistique statistique(@RequestParam(required = false) String startDate, @RequestParam(required = false) String endDate) throws EsRequestException {
        return CountApi.statistique(elasticsearchService, EsIndexEnum.PUBLICATION, ESR_CREATION_FIELD_NAME, startDate, ESR_DELETION_FIELD_NAME, endDate);
    }

    //@Cacheable("searchPublicationCache")
    @ResponseBody
    @RequestMapping(value = "/search", method = RequestMethod.POST, produces = PRODUCES_JSON)
    public SearchResponse<FullPublication> searchPublication(@RequestBody SearchRequest searchRequest) throws IOException {
        return publicationSearchService.search(searchRequest);
    }

    @ApiOperation("Finds FullPublication with given fields similar to given texts or fields of FullPublication of given id")
    @ResponseBody
    @RequestMapping(value = "/like", method = RequestMethod.POST, produces = PRODUCES_JSON)
    public LikeResponse<FullPublication> likePublication(@RequestBody LikeRequest searchRequest) throws IOException {
        return publicationSearchService.moreLikeThis(searchRequest);
    }

    //@Cacheable(value = "searchPublicationNearCache", key = "#id + \" - \" + #distance + \" - \" + #nb")
    @ResponseBody
    @ApiOperation(value = "Find publications near the given one (default to 20 nearest ones, max 100). "
            + "Does an Elasticsearch geo_distance query in FullPublication.authors.affiliations.(light)structure.(main)address "
            + "(NB. could instead use ...authors.(light)person...), see "
            + "https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-geo-distance-query.html"
            + "In v2 was rather in MongoDB", notes = "Distance in km")
    @RequestMapping(value = "/near/{id:.+}", method = RequestMethod.GET, produces = PRODUCES_JSON)
    public List<FullPublication> searchPublicationClosedTo(@PathVariable String id, @RequestParam double distance, @RequestParam(defaultValue = "20") int nb) throws IOException {
        return publicationSearchService.nearSearch(id, distance, nb);
    }

    @ApiOperation("Return all results for the current FullStructure search (no paging, all results using scroll) "
            + "in Elasticsearch with geo coordinates, in order to build the maps : "
            + "\n<br/>- of structures, by extracting all (label, address.gps filtered on main one) pairs "
            + "\n<br/>- of projects, by extracting all (projects.project.label, address.gps filtered on main one) pairs "
            + "\n<br/>- of persons, by extracting all (persons.person.fullName, address.gps filtered on main one) pairs.")
//    @Cacheable("searchGeoStructureCache")
    @ResponseBody
    @RequestMapping(value = "/search/georesults", method = RequestMethod.POST, produces = PRODUCES_JSON)
    public SearchResponse<FullPublication> geoSearch(@RequestBody SearchRequest searchRequest) throws IOException {
        return publicationSearchService.geoSearch(searchRequest);
    }

    @ApiOperation("Excel export of the search (produces content type application/vnd.ms-excel).")
    @RequestMapping(value = "/search/export", method = RequestMethod.GET, produces = "application/vnd.ms-excel")
    public void exportPublicationSearch(@RequestParam(value = "request", required = false) String searchRequestAsString,
                                        @RequestParam(value = "requestPath", required = false) String searchURLAsString, HttpServletResponse response) throws IOException {
        Date today = new Date();
        final String searchURL = ExportUtil.BASE_URL + (StringUtils.isNotEmpty(searchURLAsString) ? ExportUtil.decode(searchURLAsString) : "");

        // Deserialize the front end request
        SearchRequest searchRequest;
        if (StringUtils.isNotEmpty(searchRequestAsString)) {
            searchRequest = ExportUtil.resolveFrontString(searchRequestAsString, SearchRequest.class);
        } else {
            searchRequest = new SearchRequest();
        }

        // Set header
        response.setHeader("Content-Disposition", "attachment; filename=scanR_" + DateFormat.getDateInstance(DateFormat.SHORT).format(new Date()) + ".xls");

        ExcelExport workbook = new ExcelExport().sheet("scanR");
        // Headers
        String[] headers = {
                "Titre",
                "Sous titre",
                "Sommaire",
                "Nom de l'auteur",
                "Titre de la source",
                "Sous titre de la source",
                "Support de la source",
                "OA",
                "Editeur de la source",
                "Date de soumission",
                "Date de publication",
                "Type",
                "Label du prix",
                "Lien vers fiche scanR",
                "Date d'export",
                "Contexte de recherche",
        };
        workbook.headers(headers);

        SearchResponse<FullPublication> responses = publicationSearchService.searchExport(searchRequest, ExportUtil.EXPORT_LIMIT);

        String requestLang = searchRequest.getLang();
        // Set default if none specified
        if (requestLang == null) {
            requestLang = ApiConstants.DEFAULT_LANGUAGE;
        }
        final String lang = requestLang;

        responses.getResults().forEach(searchResult -> {
            FullPublication publication = searchResult.getValue();

            // TODO Voir sir le lien a besoin d'être modifié
            String project_link = ExportUtil.BASE_URL + "/publication/" + publication.getId();

            // Prepare data to insert
            String title = "";
            if (publication.getTitle() != null && publication.getTitle().get(lang) != null) {
                title = publication.getTitle().get(lang);
            }
            String subTitle = "";
            if (publication.getSubtitle() != null && publication.getSubtitle().get(lang) != null) {
                subTitle = publication.getSubtitle().get(lang);
            }
            String summary = "";
            if (publication.getSummary() != null && publication.getSummary().get(lang) != null) {
                summary = publication.getSummary().get(lang);
            }
            String authorFullname = "";
            if (publication.getAuthors() != null && !publication.getAuthors().isEmpty()) {
                authorFullname = publication.getAuthors().get(0).getFullName();
            }
            String sourceTitle = "";
            String sourceSubtitle = "";
            String sourceIssue = "";
            String sourcePublisher = "";
            if (publication.getSource() != null) {
                sourceTitle = publication.getSource().getTitle();
                sourceSubtitle = publication.getSource().getSubtitle();
                sourceIssue = publication.getSource().getIssue();
                sourcePublisher = publication.getSource().getPublisher();
            }
            String awardsLabel = "";
            if (publication.getAwards() != null && !publication.getAwards().isEmpty()) {
                awardsLabel = publication.getAwards().get(0).getLabel();
            }

            workbook.row()
                    .cell(summary)
                    .cell(subTitle)
                    .cell(summary)
                    .cell(authorFullname)
                    .cell(sourceTitle)
                    .cell(sourceSubtitle)
                    .cell(sourceIssue)
                    .cell(publication.getIsOa())
                    .cell(sourcePublisher)
                    .cell(publication.getSubmissionDate())
                    .cell(publication.getPublicationDate())
                    .cell(publication.getType())
                    .cell(awardsLabel)
                    .linkedCell(project_link)
                    .cell(today)
                    .linkedCell(searchURL);
        });

        workbook.autoResize();
        workbook.write(response.getOutputStream());
    }

}
