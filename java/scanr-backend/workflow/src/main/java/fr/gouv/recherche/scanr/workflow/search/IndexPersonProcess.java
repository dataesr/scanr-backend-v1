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
import fr.gouv.recherche.scanr.db.model.full.FullPerson;
import fr.gouv.recherche.scanr.db.repository.WordStemMappingRepository;
import fr.gouv.recherche.scanr.service.ElasticsearchService;
import fr.gouv.recherche.scanr.workflow.full.FullPersonService;
import fr.gouv.recherche.scanr.workflow.full.FullPersonTransaction; 

@Component
public class IndexPersonProcess extends QueueComponent implements QueueListener<String> {

    public static final MessageQueue<String> QUEUE = MessageQueue.get("INDEX_PERSON", String.class);
    private static final Logger LOGGER = LoggerFactory.getLogger(IndexPersonProcess.class);

    @Autowired
    private FullPersonService fullPersonService;

    @Autowired
    private ElasticsearchService elasticsearchService;

    @Autowired
    private WordStemMappingRepository wordStemMappingRepository;

    @Override
    public void receive(String id) {
        FullPerson fullPerson;
        try(FullPersonTransaction tx = fullPersonService.tx(id, false)) {
            if (tx == null || tx.getData() == null) {
                // Delete the person in the index
                LOGGER.info("[{}] Deleting person in Elasticsearch", id);
                elasticsearchService.getEsClient().getServicePerson().delete(id, RefreshPolicy.NONE);
                // Also delete its wordStemMapping from the DB
                wordStemMappingRepository.delete(id);
                return;
            }
            fullPerson = tx.getData();
        } catch (EsRequestException e) {
			LOGGER.error("Erreur de suppression pour la personne " + id, e);
			return;
		} 

        // Index the company
        try {
            elasticsearchService.getEsClient().getServicePerson().createOrUpdate(fullPerson, RefreshPolicy.NONE);
        } catch (EsRequestException e) {
            LOGGER.error("Une erreur est survenue lors de l'indexation de la person dans Elasticsearch : " + fullPerson.getId(), e);
        }
    }

    @Override
    public MessageQueue<String> getQueue() {
        return QUEUE;
    }
}
