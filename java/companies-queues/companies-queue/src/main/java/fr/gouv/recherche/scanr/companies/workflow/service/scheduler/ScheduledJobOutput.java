/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.companies.workflow.service.scheduler;


public class ScheduledJobOutput<E, F> extends ScheduledJobMessage<E, F> {
    public boolean reschedule = false;

    public ScheduledJobOutput(String id, E body, F status, boolean reschedule) {
        this.id = id;
        this.body = body;
        this.status = status;
        this.reschedule = reschedule;
    }

    public ScheduledJobOutput() {
    }

    public ScheduledJobOutput(String id, E body, F status) {
        this(id, body, status, false);
    }
}
