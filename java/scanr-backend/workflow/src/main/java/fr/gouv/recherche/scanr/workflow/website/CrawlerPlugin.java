/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.workflow.website;

import fr.gouv.recherche.scanr.companies.workflow.MessageQueue;
import fr.gouv.recherche.scanr.companies.workflow.service.QueueComponent;
import fr.gouv.recherche.scanr.companies.workflow.service.QueueListener;
import fr.gouv.recherche.scanr.db.model.Website;
import fr.gouv.recherche.scanr.db.repository.WebsiteRepository;
import fr.gouv.recherche.scanr.util.RepositoryLock;
import fr.gouv.recherche.scanr.workflow.website.extractor.CoreExtractorPlugin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class CrawlerPlugin extends QueueComponent implements QueueListener<CrawlerPlugin.Out> {
    public static final MessageQueue<In> QUEUE_IN = MessageQueue.get("FOCUS_CRAWLER", In.class);
    public static final MessageQueue<Out> QUEUE_OUT = MessageQueue.get("FOCUS_CRAWLER_OUT", Out.class);

    private RepositoryLock<Website, String, WebsiteRepository> repository;

    @Autowired
    private WebsiteAnalysisService service;

    @Autowired
    private CoreExtractorPlugin coreExtractorPlugin;


    public void execute(Website website) {
        In in = new In(website);
        queueService.push(in, QUEUE_IN, QUEUE_OUT);
    }

    @Override
    public void receive(Out out) {
        Website website = repository.update(Website.idFromUrl(out.url), tx -> {
            Website w = tx.getNotNull();
            w.setPageCount(out.count_page);
            tx.saveDeferred();
        }).getData();

        route(website);
    }

    private void route(Website website) {
        coreExtractorPlugin.execute(website);
    }

    @Override
    public MessageQueue<Out> getQueue() {
        return QUEUE_OUT;
    }

    @Autowired
    public void setRepository(WebsiteRepository repository) {
        this.repository = RepositoryLock.get(repository);
    }

    public static class In {
        public String url;
        public String mode;

        public In() {
        }

        public In(Website website) {
            url = website.getBaseURL();
            switch (website.getCrawlMode()) {
                case SINGLE_PAGE:
                    mode = "single";
                    break;
                case SUBPATH:
                    mode = "subpath";
                    break;
                case FULL_DOMAIN:
                    mode = "entire";
                    break;
            }
        }
    }

    public static class Out {
        public String url;
        public int count_page;
    }

}
