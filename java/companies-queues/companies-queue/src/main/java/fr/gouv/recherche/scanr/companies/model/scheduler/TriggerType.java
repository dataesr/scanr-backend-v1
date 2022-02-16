/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.companies.model.scheduler;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.scheduling.support.PeriodicTrigger;

/**
 *
 */
public enum TriggerType {
    CRON(info -> new CronTrigger(info.expression)),
    RATE(info -> {
        PeriodicTrigger trigger = new PeriodicTrigger(info.period, TimeUnit.SECONDS);
        trigger.setInitialDelay(info.initialDelay);
        return trigger;
    }),
    FIXED_RATE(info -> {
        PeriodicTrigger trigger = (PeriodicTrigger) RATE.asTrigger.apply(info);
        trigger.setFixedRate(true);
        return trigger;
    }),
    CUSTOM(null);

    private final Function<TriggerInfo, Trigger> asTrigger;

    TriggerType(Function<TriggerInfo, Trigger> asTrigger) {
        this.asTrigger = asTrigger;
    }

    public Trigger getTrigger(TriggerInfo info) {
        return asTrigger.apply(info);
    }
}
