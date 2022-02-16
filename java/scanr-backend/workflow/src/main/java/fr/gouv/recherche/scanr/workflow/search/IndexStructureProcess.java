/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.workflow.search;

import java.util.List;
import java.util.stream.Collectors;

import org.elasticsearch.action.support.WriteRequest.RefreshPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sword.utils.elasticsearch.exceptions.EsRequestException;

import fr.gouv.recherche.scanr.companies.workflow.MessageQueue;
import fr.gouv.recherche.scanr.companies.workflow.service.QueueComponent;
import fr.gouv.recherche.scanr.companies.workflow.service.QueueListener;
import fr.gouv.recherche.scanr.crawl.CrawlStoreService;
import fr.gouv.recherche.scanr.db.model.full.FullStructure;
import fr.gouv.recherche.scanr.db.repository.WordStemMappingRepository;
import fr.gouv.recherche.scanr.search.model.WebPage;
import fr.gouv.recherche.scanr.service.ElasticsearchService;
import fr.gouv.recherche.scanr.workflow.full.FullStructureService;
import fr.gouv.recherche.scanr.workflow.full.FullStructureTransaction;

@Component
public class IndexStructureProcess extends QueueComponent implements QueueListener<String> {
    public static final MessageQueue<String> QUEUE = MessageQueue.get("INDEX_STRUCTURE", String.class);
    private static final Logger LOGGER = LoggerFactory.getLogger(IndexStructureProcess.class);

    @Autowired
    private FullStructureService fsService;

    @Autowired
    private ElasticsearchService elasticsearchService;

    @Autowired
    private WordStemMappingRepository wordStemMappingRepository;

    @Autowired
    private CrawlStoreService crawlStore;

    @Override
    public void receive(String id) {
        FullStructure fullStructure;
        try(FullStructureTransaction tx = fsService.tx(id, false)) {
            if (tx == null || tx.getData() == null) {
                // Delete the structure in the index
                LOGGER.info("[{}] Deleting structure in Elasticsearch", id);
                elasticsearchService.getEsClient().getServiceStructure().delete(id, RefreshPolicy.NONE);
                // Also delete its wordStemMapping from the DB
                wordStemMappingRepository.delete(id);
                return;
            }
            fullStructure = tx.getData();
        } catch (EsRequestException e) {
			LOGGER.error("Erreur de suppression pour la structure " + id, e);
			return;
		} 

        // Get webpages for websites
        if (fullStructure.getWebsites() != null && fullStructure.getWebsites().size() > 0) {
            fullStructure.getWebsites().stream().forEach(website -> {
                // Get crawl id
                String baseURL = website.getBaseURL();
                final String crawlId = this.crawlStore.searchCrawlId(baseURL);
                List<WebPage> pages = null;
                if (crawlId != null) {
                    pages = this.crawlStore.getCrawlTexts(crawlId).stream().map(page -> new WebPage(page.getTitle(), page.getContent())).collect(Collectors.toList());
                    LOGGER.trace("Got crawl " + crawlId + " from crawl store with " + pages.size() + " pages.");
                } else {
                    LOGGER.error("No crawlID for structure [" + fullStructure.getId() + "] with url [" + baseURL + "]");
                }

                website.setWebPages(pages);
            });
        }

        // Index the company
        try {
            elasticsearchService.getEsClient().getServiceStructure().createOrUpdate(fullStructure, RefreshPolicy.NONE);
        } catch (EsRequestException e) {
            LOGGER.error("Une erreur est survenue lors de l'indexation de la structure dans Elasticsearch : " + fullStructure.getId(), e);
        }
    }

    @Override
    public MessageQueue<String> getQueue() {
        return QUEUE;
    }
}
