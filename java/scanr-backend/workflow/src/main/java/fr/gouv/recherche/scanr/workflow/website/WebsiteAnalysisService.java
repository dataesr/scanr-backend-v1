/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.workflow.website;

import fr.gouv.recherche.scanr.db.model.CrawlMode;
import fr.gouv.recherche.scanr.db.model.Website;
import fr.gouv.recherche.scanr.db.model.full.FullStructureField;
import fr.gouv.recherche.scanr.db.repository.StructureRepository;
import fr.gouv.recherche.scanr.db.repository.WebsiteRepository;
import fr.gouv.recherche.scanr.util.RepositoryLock;
import fr.gouv.recherche.scanr.workflow.full.FullStructureService;
import fr.gouv.recherche.scanr.workflow.website.screenshot.ScreenshotPlugin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;


@Service
public class WebsiteAnalysisService {

    private RepositoryLock<Website, String, WebsiteRepository> websiteRepository;

    @Autowired
    private StructureRepository repository;
    @Autowired
    private FullStructureService fullStructureService;
    @Autowired
    private CrawlerPlugin crawlerPlugin;
    @Autowired
    private ScreenshotPlugin screenshot;

    public Website analyze(String url, CrawlMode mode, boolean recrawl) {
        String id = Website.idFromUrl(url);

        return websiteRepository.update(id, tx -> {
            Website w = tx.get();
            boolean doCrawl = recrawl;
            if (w == null) {
                // new website
                w = new Website(id, url, mode);
                tx.saveDeferred(w);
                doCrawl = true;
            } else if (w.getCrawlMode() != mode) {
                doCrawl = true;
                w.setCrawlMode(mode);
                tx.saveDeferred();
            }
            if (doCrawl) {
                crawlerPlugin.execute(w);
            }
        }).getData();
    }

    public void analysisEnd(String websiteId) {
        // Ack the end of the analysis
        RepositoryLock<Website, String, WebsiteRepository>.TxResult<Boolean> result = websiteRepository.updateAndReturn(websiteId, tx -> {
            boolean isNew = tx.get().getLastCompletion() == null;
            tx.get().setLastCompletion(new Date());
            tx.saveDeferred();
            return isNew;
        });
        Website w = result.getData();
        refreshFSForWebsite(websiteId);

        // Launch the screenshot
        screenshot.execute(w.getBaseURL());

    }

    public void refreshFSForWebsite(String websiteId) {
        fullStructureService.refreshWebsite(websiteId);
    }

    @Autowired
    public void setWebsiteRepository(WebsiteRepository websiteRepository) {
        this.websiteRepository = RepositoryLock.get(websiteRepository);
    }
}
