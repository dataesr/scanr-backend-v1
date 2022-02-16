/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.api;

import fr.gouv.recherche.scanr.api.util.ApiConstants;
import fr.gouv.recherche.scanr.companies.workflow.service.scheduler.QueueScheduler;
import fr.gouv.recherche.scanr.db.model.CrawlMode;
import fr.gouv.recherche.scanr.db.model.Link;
import fr.gouv.recherche.scanr.db.model.Structure;
import fr.gouv.recherche.scanr.db.model.Website;
import fr.gouv.recherche.scanr.db.repository.StructureRepository;
import fr.gouv.recherche.scanr.db.repository.WebsiteRepository;
import fr.gouv.recherche.scanr.workflow.menesr.MenesrImportService;
import fr.gouv.recherche.scanr.workflow.website.CrawlerPlugin;
import fr.gouv.recherche.scanr.workflow.website.WebsiteAnalysisService;
import fr.gouv.recherche.scanr.workflow.website.extractor.CoreExtractorPlugin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;


@Controller
@RequestMapping("/services/website")
public class WebsiteAnalysisApi {
    @Autowired
    private CoreExtractorPlugin coreExtractorPlugin;

    @Autowired
    private CrawlerPlugin crawlerPlugin;

    @Autowired
    private StructureRepository structureRepository;

    @Autowired
    private WebsiteAnalysisService WebsiteAnalysisService;

    @Autowired
    private WebsiteRepository websiteRepository;

    @Autowired
    private QueueScheduler queueScheduler;

    @ResponseBody
    @RequestMapping(value = "/recrawl", method = RequestMethod.POST, produces = ApiConstants.PRODUCES_JSON)
    public long crawl() {
        return forEachWebsite(website -> crawlerPlugin.execute(website));
    }

    @ResponseBody
    @RequestMapping(value = "/extract", method = RequestMethod.POST, produces = ApiConstants.PRODUCES_JSON)
    public long extract() {
        return forEachWebsite(website -> coreExtractorPlugin.execute(website));
    }

    @ResponseBody
    @RequestMapping(value = "/recrawl/{id}", method = RequestMethod.POST, produces = ApiConstants.PRODUCES_JSON)
    public void crawlStructure(@PathVariable String id) {
        Structure structure = structureRepository.findOne(id);
        if (structure == null) throw new IllegalArgumentException("Cannot find structure " + id);

        List<Link> links = structure.getLinks().stream().filter(Objects::nonNull).collect(Collectors.toList());
        for (Link link : links) {
            CrawlMode mode = link.getMode();
            if (mode == null) {
                mode = MenesrImportService.inferCrawlMode(link.getId());
            }
            WebsiteAnalysisService.analyze(link.getUrl(), mode, false);
        }
    }



    private long forEachWebsite(Consumer<Website> action) {
        return websiteRepository.streamAll().peek(action).count();
    }
}
