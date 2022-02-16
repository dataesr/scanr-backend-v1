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
import fr.gouv.recherche.scanr.db.model.full.FullPublication;
import fr.gouv.recherche.scanr.db.repository.WordStemMappingRepository;
import fr.gouv.recherche.scanr.service.ElasticsearchService;
import fr.gouv.recherche.scanr.workflow.full.FullPublicationService;
import fr.gouv.recherche.scanr.workflow.full.FullPublicationTransaction;

@Component
public class IndexPublicationProcess extends QueueComponent implements QueueListener<String> {

    public static final MessageQueue<String> QUEUE = MessageQueue.get("INDEX_PUBLICATION", String.class);
    private static final Logger LOGGER = LoggerFactory.getLogger(IndexPublicationProcess.class);

    @Autowired
    private FullPublicationService fullPublicationService;

    @Autowired
    private ElasticsearchService elasticsearchService;

    @Autowired
    private WordStemMappingRepository wordStemMappingRepository;

    @Override
    public void receive(String id) {
        FullPublication fullPublication;
        try(FullPublicationTransaction tx = fullPublicationService.tx(id, false)) {
            if (tx == null || tx.getData() == null) {
                // Delete the publication in the index
                LOGGER.info("[{}] Deleting publication in Elasticsearch", id);
                elasticsearchService.getEsClient().getServicePublication().delete(id, RefreshPolicy.NONE);
                // Also delete its wordStemMapping from the DB
                wordStemMappingRepository.delete(id);
                return;
            }
            fullPublication = tx.getData();
        } catch (EsRequestException e) {
			LOGGER.error("Erreur de suppression pour la publication " + id, e);
			return;
		} 

        // Index the company
        try {
            elasticsearchService.getEsClient().getServicePublication().createOrUpdate(fullPublication, RefreshPolicy.NONE);
        } catch (EsRequestException e) {
            LOGGER.error("Une erreur est survenue lors de l'indexation de la publication dans Elasticsearch : " + fullPublication.getId(), e);
        }
    }

    @Override
    public MessageQueue<String> getQueue() {
        return QUEUE;
    }
}
