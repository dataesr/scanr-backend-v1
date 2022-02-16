/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.companies.mock;

import fr.gouv.recherche.scanr.companies.workflow.service.scheduler.ScheduledJobInput;
import fr.gouv.recherche.scanr.companies.workflow.service.scheduler.ScheduledJobOutput;

import java.util.Date;
import java.util.List;

/**
 *
 */
public class RssOut extends ScheduledJobOutput<RssOut.Body, Date> {
    public static class Body {
        public String url;
        public Date lastUpdated;
        public String title;
        public List<String> items;
        public Stats rate;
    }

    public RssOut(ScheduledJobInput message, Body body, Date date) {
        super(message.id, body, date);
    }

    public RssOut() {
    }

    public static class Stats {
        public Long min;
        public Long max;
        public Long avg;
    }
}
