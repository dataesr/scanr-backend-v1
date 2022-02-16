/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.companies.workflow.service;

public class QueueComponent {
    protected QueueService queueService;

    public void ready(QueueService service) {
        this.queueService = service;
    }
}
