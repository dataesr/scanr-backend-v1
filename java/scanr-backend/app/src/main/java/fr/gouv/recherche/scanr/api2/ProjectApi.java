/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */

package fr.gouv.recherche.scanr.api2;

import static fr.gouv.recherche.scanr.api.util.ApiConstants.PRODUCES_JSON;

import java.io.IOException;
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
import fr.gouv.recherche.scanr.db.model.full.FullProject;
import fr.gouv.recherche.scanr.search.model2.request.LikeRequest;
import fr.gouv.recherche.scanr.search.model2.request.SearchRequest;
import fr.gouv.recherche.scanr.search.model2.response.LikeResponse;
import fr.gouv.recherche.scanr.search.model2.response.SearchResponse;
import fr.gouv.recherche.scanr.search.service.ProjectSearchService;
import fr.gouv.recherche.scanr.service.ElasticsearchService;
import fr.gouv.recherche.scanr.util.ExcelExport;
import io.swagger.annotations.ApiOperation;

@Controller("projectApi2")
@RequestMapping("/v2/projects/")
public class ProjectApi {

    private static final String ESR_CREATION_FIELD_NAME = "createdAt";
    private static final String ESR_DELETION_FIELD_NAME = "removedAt";

    @Autowired
    private ProjectSearchService projectSearchService;

    @Autowired
    private ElasticsearchService elasticsearchService;

    @ApiOperation("Gets by id in Elasticsearch")
    @ResponseBody
    @RequestMapping(value = "/{id:.+}", method = RequestMethod.GET, produces = PRODUCES_JSON)
    public FullProject getProject(@UserLocale List<Locale> locales, @PathVariable String id) throws EsRequestException {
        return elasticsearchService.getEsClient().getServiceProject().get(id);
    }

    @ResponseBody
    @RequestMapping(value = "/statistics", method = RequestMethod.POST, produces = PRODUCES_JSON)
    public Statistique statistique(@RequestParam(required = false) String startDate, @RequestParam(required = false) String endDate) throws EsRequestException {
        return CountApi.statistique(elasticsearchService, EsIndexEnum.PROJECT, ESR_CREATION_FIELD_NAME, startDate, ESR_DELETION_FIELD_NAME, endDate);
    }

    //@Cacheable("searchProjectCache")
    @ResponseBody
    @RequestMapping(value = "/search", method = RequestMethod.POST, produces = PRODUCES_JSON)
    public SearchResponse<FullProject> searchProject(@RequestBody SearchRequest searchRequest) throws IOException {
        return projectSearchService.search(searchRequest);
    }

    @ApiOperation("Finds FullProject with given fields similar to given texts or fields of FullProject of given id")
    @ResponseBody
    @RequestMapping(value = "/like", method = RequestMethod.POST, produces = PRODUCES_JSON)
    public LikeResponse<FullProject> likeProject(@RequestBody LikeRequest searchRequest) throws IOException {
        return projectSearchService.moreLikeThis(searchRequest);
    }

    //Cacheable(value = "searchProjectNearCache", key = "#id + \" - \" + #distance + \" - \" + #nb")
    @ResponseBody
    @ApiOperation(value = "Find projects near the given one (default to 20 nearest ones, max 100). "
            + "Does an Elasticsearch geo_distance query in FullProject.participants.(light)structure.(main)address, see "
            + "https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-geo-distance-query.html"
            + "In v2 was rather in MongoDB", notes = "Distance in km")
    @RequestMapping(value = "/near/{id:.+}", method = RequestMethod.GET, produces = PRODUCES_JSON)
    public List<FullProject> searchProjectClosedTo(@PathVariable String id, @RequestParam double distance, @RequestParam(defaultValue = "20") int nb) throws IOException {
        return projectSearchService.nearSearch(id, distance, nb);
    }

    @ApiOperation("Return all results for the current FullStructure search (no paging, all results using scroll) "
            + "in Elasticsearch with geo coordinates, in order to build the maps : "
            + "\n<br/>- of structures, by extracting all (label, address.gps filtered on main one) pairs "
            + "\n<br/>- of projects, by extracting all (projects.project.label, address.gps filtered on main one) pairs "
            + "\n<br/>- of persons, by extracting all (persons.person.fullName, address.gps filtered on main one) pairs.")
//    @Cacheable("searchGeoStructureCache")
    @ResponseBody
    @RequestMapping(value = "/search/georesults", method = RequestMethod.POST, produces = PRODUCES_JSON)
    public SearchResponse<FullProject> geoSearch(@RequestBody SearchRequest searchRequest) throws IOException {
        return projectSearchService.geoSearch(searchRequest);
    }

    @ApiOperation("Excel export of the search (produces content type application/vnd.ms-excel).")
    @RequestMapping(value = "/search/export", method = RequestMethod.GET, produces = "application/vnd.ms-excel")
    public void exportProjectSearch(@RequestParam(value = "request", required = false) String searchRequestAsString,
                                    @RequestParam(value = "requestPath", required = false) String searchURLAsString, HttpServletResponse response) throws IOException {
        Date today = new Date();
        final String searchURL = ExportUtil.BASE_URL + (StringUtils.isNotEmpty(searchURLAsString) ? ExportUtil.decode(searchURLAsString) : "");

        // Deserialize the front end request
        SearchRequest searchRequest;
        if (StringUtils.isNotEmpty(searchRequestAsString)) {
            searchRequest = ExportUtil.resolveFrontString(searchRequestAsString, SearchRequest.class);
        }
        else {
            searchRequest = new SearchRequest();
        }

        // Set header
        response.setHeader("Content-Disposition", "attachment; filename=scanR_" + DateFormat.getDateInstance(DateFormat.SHORT).format(new Date()) + ".xls");

        ExcelExport workbook = new ExcelExport().sheet("scanR");
        // Headers
        String[] headers = {
                "Label",
                "Type",
                "Date de début",
                "Date de fin",
                "Budget total",
                "Budget financé",
                "URL",
                "URL du project",
                "Nombre de participant",
                "Nom de l'appel à projet",
                "Label de l'action",
                "Durée",
                "Lien vers fiche scanR",
                "Date d'export",
                "Contexte de recherche",
        };
        workbook.headers(headers);

        SearchResponse<FullProject> responses = projectSearchService.searchExport(searchRequest, ExportUtil.EXPORT_LIMIT);

        String requestLang = searchRequest.getLang();
        // Set default if none specified
        if (requestLang == null) {
            requestLang = ApiConstants.DEFAULT_LANGUAGE;
        }
        final String lang = requestLang;

        responses.getResults().forEach(searchResult -> {
            FullProject project = searchResult.getValue();

            // TODO Voir sir le lien a besoin d'être modifié
            String project_link = ExportUtil.BASE_URL + "/project/" + project.getId();
            String label = "";
            if (project.getLabel() != null && project.getLabel().get(lang) != null) {
                label = project.getLabel().get(lang);
            }
            String callLabel = "";
            if (project.getCall() != null && project.getCall().getLabel() != null) {
                callLabel = project.getCall().getLabel();
            }
            String actionLabel = "";
            if (project.getAction() != null && project.getAction().getLabel() != null) {
                actionLabel = project.getAction().getLabel().get(lang);
            }

            workbook.row()
                    .cell(label)
                    .cell(project.getType())
                    .cell(project.getStartDate())
                    .cell(project.getEndDate())
                    .cell(project.getBudgetTotal())
                    .cell(project.getBudgetFinanced())
                    .linkedCell(project.getUrl())
                    .linkedCell(project.getProjectUrl())
                    .cell(project.getParticipantCount())
                    .cell(callLabel)
                    .cell(actionLabel)
                    .cell(project.getDuration())
                    .linkedCell(project_link)
                    .cell(today)
                    .linkedCell(searchURL);
        });

        workbook.autoResize();
        workbook.write(response.getOutputStream());
    }

}
