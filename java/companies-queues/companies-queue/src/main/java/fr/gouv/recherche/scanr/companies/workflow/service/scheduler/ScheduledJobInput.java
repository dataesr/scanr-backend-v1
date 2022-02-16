/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.companies.workflow.service.scheduler;

import java.util.Date;

/**
 *
 */
public class ScheduledJobInput<E, F> extends ScheduledJobMessage<E, F> {
    public Date timestamp;
    public Date lastExecution;

    public ScheduledJobInput(String id, Date timestamp, Date lastExecution, E body, F status) {
        this.id = id;
        this.timestamp = timestamp;
        this.lastExecution = lastExecution;
        this.body = body;
        this.status = status;
    }

    public ScheduledJobInput() {
    }
}
