/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.companies.workflow.service;

import fr.gouv.recherche.scanr.companies.workflow.MessageQueue;

public interface QueueSubscriber<DTO> {
    /**
     * Get the number of consumers for the message listener
     *
     * @return the number of consumers
     */
    default int getConcurrentConsumers() {
        return 1;
    }

    /**
     * The queue to listen to
     *
     * @return The queue
     */
    MessageQueue<DTO> getQueue();

    /**
     * Define the consumer timeout
     *
     * @return the receiveTimeout. null is driver's default.
     */
    default Long receiveTimeout() {
        return null;
    }
}
