/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.workflow.menesr;

import fr.gouv.recherche.scanr.companies.workflow.MessageQueue;
import fr.gouv.recherche.scanr.companies.workflow.service.PluginService;
import fr.gouv.recherche.scanr.companies.workflow.service.QueueComponent;
import fr.gouv.recherche.scanr.companies.workflow.service.scheduler.ScheduledJobInput;
import fr.gouv.recherche.scanr.companies.workflow.service.scheduler.ScheduledJobMessage;
import fr.gouv.recherche.scanr.db.repository.WebsiteRepository;
import fr.gouv.recherche.scanr.workflow.website.CrawlerPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;


@Component
public class RecrawlProcess extends QueueComponent implements PluginService<RecrawlProcess.RecrawlOrder, ScheduledJobMessage<String, Date>> {
    public static final String PROVIDER = "crawl";
    public static final String ID = "all";
    public static final MessageQueue<RecrawlOrder> QUEUE = MessageQueue.get("WEBSITE_RECRAWL_ALL", RecrawlOrder.class);

    private static final Logger log = LoggerFactory.getLogger(RecrawlProcess.class);

    @Autowired
    private CrawlerPlugin crawlerPlugin;

    @Autowired
    private WebsiteRepository websiteRepository;

    @Override
    public ScheduledJobMessage<String, Date> receiveAndReply(RecrawlOrder message) {
        if (message.body.equals(ID)) {
            websiteRepository.streamAll().forEach(website -> crawlerPlugin.execute(website));
        }
        return message;
    }

    @Override
    public MessageQueue<RecrawlOrder> getQueue() {
        return QUEUE;
    }

    public static class RecrawlOrder extends ScheduledJobInput<String, Date> {
    }

}
