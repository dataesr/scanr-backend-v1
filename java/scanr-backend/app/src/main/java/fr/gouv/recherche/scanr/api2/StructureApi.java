/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.api2;

import static fr.gouv.recherche.scanr.api.util.ApiConstants.PRODUCES_JSON;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sword.utils.elasticsearch.exceptions.EsRequestException;

import fr.gouv.recherche.scanr.api.exception.NotFoundException;
import fr.gouv.recherche.scanr.api.util.ApiConstants;
import fr.gouv.recherche.scanr.api.util.ExportUtil;
import fr.gouv.recherche.scanr.api.util.UserLocale;
import fr.gouv.recherche.scanr.config.elasticsearch.EsIndexEnum;
import fr.gouv.recherche.scanr.db.model.Address;
import fr.gouv.recherche.scanr.db.model.Link;
import fr.gouv.recherche.scanr.db.model.Statistique;
import fr.gouv.recherche.scanr.db.model.Structure;
import fr.gouv.recherche.scanr.db.model.Website;
import fr.gouv.recherche.scanr.db.model.full.FullStructure;
import fr.gouv.recherche.scanr.search.model2.request.LikeRequest;
import fr.gouv.recherche.scanr.search.model2.request.SearchRequest;
import fr.gouv.recherche.scanr.search.model2.response.LikeResponse;
import fr.gouv.recherche.scanr.search.model2.response.SearchResponse;
import fr.gouv.recherche.scanr.search.service.StructureSearchService;
import fr.gouv.recherche.scanr.service.ElasticsearchService;
import fr.gouv.recherche.scanr.service.ScreenshotStorageService;
import fr.gouv.recherche.scanr.util.ElasticsearchDateUtils;
import fr.gouv.recherche.scanr.util.ExcelExport;
import io.swagger.annotations.ApiOperation;


/**
 * Kept only for screenshot, which is not specific to Structure.
 * 
 * Get by id APIs have been moved to SearchApi and now work on Elasticsearch and not on MongoDB anymore.
 * Both contain almost the same structured data anyway, Elasticsearch has more website
 * (but they can now be asked not to be returned) and normalization.
 */
@Controller("structureApi2")
@RequestMapping("/v2/structures/")
public class StructureApi {

    private static final Logger log = LoggerFactory.getLogger(StructureApi.class);

    private static final String IS_FRENCH_FIELD_NAME = "isFrench";
    private static final String ESR_CREATION_FIELD_NAME = "createdAt";
    private static final String ESR_DELETION_FIELD_NAME = "removedAt";

    @Autowired
    private ElasticsearchService elasticsearchService;

    @Autowired
    private StructureSearchService structureSearchService;
    @Autowired
    private ScreenshotStorageService screenshotStorageService;

    @ResponseBody
    @RequestMapping(value = "/statistics", method = RequestMethod.POST, produces = PRODUCES_JSON)
    public Statistique statistique(@RequestParam(required = false) String startDate, @RequestParam(required = false) String endDate) throws EsRequestException {
        Statistique statistique = new Statistique();
        // Foreign structure are not taken into account
        TermQueryBuilder termQueryBuilder = new TermQueryBuilder(IS_FRENCH_FIELD_NAME, true);

        try {
            startDate = ElasticsearchDateUtils.getFormattedDateForESFromString(startDate);
            endDate = ElasticsearchDateUtils.getFormattedDateForESFromString(endDate);
        } catch (ParseException e) {
            log.error("Date parsing error for statistics API", e);
        }

        if (startDate != null) {
            BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
            boolQueryBuilder.must(termQueryBuilder);
            RangeQueryBuilder queryCreated = new RangeQueryBuilder(ESR_CREATION_FIELD_NAME);
            queryCreated.lte(endDate);
            queryCreated.gte(startDate);
            boolQueryBuilder.must(queryCreated);
            long nbCreated = elasticsearchService.getEsClient().getServiceGeneral().count(EsIndexEnum.STRUCTURE, boolQueryBuilder);
            statistique.setNbCreated(nbCreated);
        }

        if (endDate !=  null) {
            BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
            boolQueryBuilder.must(termQueryBuilder);
            RangeQueryBuilder queryDeleted = new RangeQueryBuilder(ESR_DELETION_FIELD_NAME);
            queryDeleted.lte(endDate);
            queryDeleted.gte(startDate);
            boolQueryBuilder.must(queryDeleted);
            long nbDeleted = elasticsearchService.getEsClient().getServiceGeneral().count(EsIndexEnum.STRUCTURE, boolQueryBuilder);
            statistique.setNbDeleted(nbDeleted);
        }

        statistique.setDiffCreationDeleted(statistique.getNbCreated() - statistique.getNbDeleted());

        return statistique;
    }

    @ApiOperation("Gets by id in Elasticsearch")
    @ResponseBody
    @RequestMapping(value = "/structure/{id:.+}", method = RequestMethod.GET, produces = PRODUCES_JSON)
    public FullStructure getStructure(@UserLocale List<Locale> locales, @PathVariable String id) throws EsRequestException {
        List<String> excludedFields = Arrays.asList(
                FullStructure.FIELDS.WEBSITES.TITLE,
                FullStructure.FIELDS.PROJECTS.TITLE,
                FullStructure.FIELDS.PUBLICATIONS.FIELDNAME
        );
        
        return elasticsearchService.getEsClient().getServiceStructure().get(id, null, excludedFields);
    }

    @ApiOperation("Finds FullStructure with given fields similar to given texts or fields of FullStructure of given id")
    @ResponseBody
    @RequestMapping(value = "/like", method = RequestMethod.POST, produces = PRODUCES_JSON)
    public LikeResponse<FullStructure> likeStructure(@RequestBody LikeRequest searchRequest) throws IOException {
        return structureSearchService.moreLikeThis(searchRequest);
    }

    //    @Cacheable("searchStructureCache")
    @ResponseBody
    @RequestMapping(value = "/search", method = RequestMethod.POST, produces = PRODUCES_JSON)
    public SearchResponse<FullStructure> searchStructure(@RequestBody SearchRequest searchRequest) throws IOException {
        return structureSearchService.search(searchRequest);
    }

    //    @Cacheable(value = "searchStructureNearCache", key = "#id + \" - \" + #distance + \" - \" + #nb")
    @ResponseBody
    @ApiOperation(value = "Find structures near the given one (default to 20 nearest ones, max 100). "
            + "Does an Elasticsearch geo_distance query in FullStructure.(main)address, see "
            + "https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-geo-distance-query.html "
            + "In v2 was rather in MongoDB", notes = "Distance in km")
    @RequestMapping(value = "/near/{id:.+}", method = RequestMethod.GET, produces = PRODUCES_JSON)
    public List<Structure> searchStructureClosedTo(@PathVariable String id, @RequestParam double distance, @RequestParam(defaultValue = "20") int nb) throws IOException {
        return structureSearchService.nearSearch(id, distance, nb);
    }

    @ApiOperation("Return all results for the current FullStructure search (no paging, all results using scroll) "
            + "in Elasticsearch with geo coordinates, in order to build the maps : "
            + "\n<br/>- of structures, by extracting all (label, address.gps filtered on main one) pairs "
            + "\n<br/>- of projects, by extracting all (projects.project.label, address.gps filtered on main one) pairs "
            + "\n<br/>- of persons, by extracting all (persons.person.fullName, address.gps filtered on main one) pairs.")
//    @Cacheable("searchGeoStructureCache")
    @ResponseBody
    @RequestMapping(value = "/search/georesults", method = RequestMethod.POST, produces = PRODUCES_JSON)
    public SearchResponse<FullStructure> geoSearchStructure(@RequestBody SearchRequest searchRequest) throws IOException {
        return structureSearchService.geoSearch(searchRequest);
    }

    @ResponseBody
    @RequestMapping(value = "/screenshot/{id:.+}", method = RequestMethod.GET, produces = "image/png")
    public void screenshot(@PathVariable String id, HttpServletResponse response) throws EsRequestException, IOException {
        FullStructure fs = elasticsearchService.getEsClient().getServiceStructure().get(id);

        // Structure non trouvée
        if (fs == null) {
            response.setStatus(404);
            return;
        }

        List<Link> links = fs.getLinks();
        // Si pas de lien alors aucun screenshot à retourner
        if (links == null || links.isEmpty()) {
            response.setStatus(204);
            return;
        }

        // Récupèration du website principal
        Optional<Link> mainLink = Optional.empty();
        for (Link link : links) {
            if (Link.MAIN_TYPE.equals(link.getType())) {
                mainLink = Optional.of(link);
                break;
            }
        }

        // Si pas de lien alors aucun screenshot à retourner
        if (!mainLink.isPresent()) {
            response.setStatus(204);
            return;
        }

        try {
            byte[] bytes = screenshotStorageService.get(mainLink.get().getId());
            response.setStatus(200);
            response.getOutputStream().write(bytes);
        } catch (NotFoundException nfe) {
            response.setStatus(404);
        }
    }

    @ApiOperation("Excel export of the search (produces content type application/vnd.ms-excel).")
    @RequestMapping(value = "/search/export", method = RequestMethod.GET, produces = "application/vnd.ms-excel")
    public void exportStructureSearch(@RequestParam(value = "request", required = false) String searchRequestAsString,
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
                "Identifiant",
                "Label",
                "Acronyme",
                "Nature",
                "Allias",
                "Code postal",
                "Ville",
                "Site internet",
                "Lien vers fiche scanR",
                "Date d'export",
                "Contexte de recherche",
        };
        workbook.headers(headers);

        SearchResponse<FullStructure> responses = structureSearchService.searchExport(searchRequest, ExportUtil.EXPORT_LIMIT);

        String requestLang = searchRequest.getLang();
        // Set default if none specified
        if (requestLang == null) {
            requestLang = ApiConstants.DEFAULT_LANGUAGE;
        }
        final String lang = requestLang;

        responses.getResults().forEach(searchResult -> {
            FullStructure structure = searchResult.getValue();
            Address address = null;
            if (structure.getAddress() != null && !structure.getAddress().isEmpty()) {
                address = structure.getAddress().get(0);
            }
            Website website = null;
            if (structure.getWebsites() != null && !structure.getWebsites().isEmpty()) {
                website = structure.getWebsites().get(0);
            }

            // TODO Voir sir le lien a besoin d'être modifié
            String stucture_link = ExportUtil.BASE_URL + "/structure/" + structure.getId();
            String label = "";
            if (structure.getLabel() != null && structure.getLabel().get(lang) != null) {
                label = structure.getLabel().get(lang);
            }
            String acronyme = "";
            if (structure.getAcronym() != null && structure.getAcronym().get(lang) != null) {
                acronyme = structure.getAcronym().get(lang);
            }

            workbook.row()
                    .cell(structure.getId())
                    .cell(label)
                    .cell(acronyme)
                    .cell(structure.getNature())
                    .cell(CollectionUtils.isEmpty(structure.getAlias()) ? "" : structure.getAlias().get(0))
                    .cell(address == null ? "" : address.getPostcode())
                    .cell(address == null ? "" : address.getCity())
                    .cell(website == null ? "" : website.getBaseURL())
                    .linkedCell(stucture_link)
                    .cell(today)
                    .linkedCell(searchURL);
        });

        workbook.autoResize();
        workbook.write(response.getOutputStream());
    }

}
