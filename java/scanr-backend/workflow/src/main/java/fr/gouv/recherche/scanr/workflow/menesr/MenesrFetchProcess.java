/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.workflow.menesr;

import fr.gouv.recherche.scanr.companies.workflow.MessageQueue;
import fr.gouv.recherche.scanr.companies.workflow.service.PluginService;
import fr.gouv.recherche.scanr.companies.workflow.service.scheduler.ScheduledJobInput;
import fr.gouv.recherche.scanr.companies.workflow.service.scheduler.ScheduledJobMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;


@Component
public class MenesrFetchProcess implements PluginService<MenesrFetchProcess.FetchOrder, ScheduledJobMessage<MenesrFetchProcess.FetchType, Date>> {
    public static final String PROVIDER = "menesr";
    public static final String ID_SPPP = "sppp";

    public static final MessageQueue<FetchOrder> QUEUE = MessageQueue.get("menesr", MenesrFetchProcess.FetchOrder.class);
    private static final Logger log = LoggerFactory.getLogger(RecrawlProcess.class);

    @Autowired
    private MenesrImportService service;

    @Override
    public ScheduledJobMessage<FetchType, Date> receiveAndReply(FetchOrder message) {
        switch (message.body) {
            case STRUCTURE_PUBLICATION_PROJECT_PERSON:
                log.info("Fetch Structures");
                service.fetchStructures();

                log.info("Fetch Publications");
                service.fetchPublications();

                log.info("Fetch Projects");
                service.fetchProjects();

                log.info("Fetch Persons");
                service.fetchPersons();

                log.info("Finished menesr fetch");
                break;
        }
        return message;
    }

    @Override
    public MessageQueue<FetchOrder> getQueue() {
        return QUEUE;
    }

    public enum FetchType {
        STRUCTURE_PUBLICATION_PROJECT_PERSON,
    }

    public static class FetchOrder extends ScheduledJobInput<FetchType, Date> {
    }
}
