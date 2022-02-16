/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */

package fr.gouv.recherche.scanr.workflow.search;

import org.elasticsearch.action.support.WriteRequest.RefreshPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sword.utils.elasticsearch.exceptions.EsRequestException;

import fr.gouv.recherche.scanr.companies.workflow.MessageQueue;
import fr.gouv.recherche.scanr.companies.workflow.service.QueueComponent;
import fr.gouv.recherche.scanr.companies.workflow.service.QueueListener;
import fr.gouv.recherche.scanr.db.model.full.FullProject;
import fr.gouv.recherche.scanr.db.repository.WordStemMappingRepository;
import fr.gouv.recherche.scanr.service.ElasticsearchService;
import fr.gouv.recherche.scanr.workflow.full.FullProjectService;
import fr.gouv.recherche.scanr.workflow.full.FullProjectTransaction;

@Component
public class IndexProjectProcess extends QueueComponent implements QueueListener<String> {

    public static final MessageQueue<String> QUEUE = MessageQueue.get("INDEX_PROJECT", String.class);
    private static final Logger LOGGER = LoggerFactory.getLogger(IndexProjectProcess.class);

    @Autowired
    private FullProjectService fullProjectService;

    @Autowired
    private ElasticsearchService elasticsearchService;

    @Autowired
    private WordStemMappingRepository wordStemMappingRepository;

    @Override
    public void receive(String id) {
        FullProject fullProject;
        try(FullProjectTransaction tx = fullProjectService.tx(id, false)) {
            if (tx == null || tx.getData() == null) {
                // Delete the project in the index
                LOGGER.info("[{}] Deleting project in Elasticsearch", id);
                elasticsearchService.getEsClient().getServiceProject().delete(id, RefreshPolicy.NONE);
                // Also delete its wordStemMapping from the DB
                wordStemMappingRepository.delete(id);
                return;
            }
            fullProject = tx.getData();
        } catch (EsRequestException e) {
			LOGGER.error("Erreur de suppression pour le projet " + id, e);
			return;
		} 

        // Index the company
        try {
            elasticsearchService.getEsClient().getServiceProject().createOrUpdate(fullProject, RefreshPolicy.NONE);
        } catch (EsRequestException e) {
            LOGGER.error("Une erreur est survenue lors de l'indexation du project dans Elasticsearch : " + fullProject.getId(), e);
        }
    }

    @Override
    public MessageQueue<String> getQueue() {
        return QUEUE;
    }
}
