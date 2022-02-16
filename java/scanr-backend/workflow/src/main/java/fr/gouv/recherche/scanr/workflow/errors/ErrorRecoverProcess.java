/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.workflow.errors;

import fr.gouv.recherche.scanr.companies.model.error.ErrorMessage;
import fr.gouv.recherche.scanr.companies.repository.mongo.ErrorRepository;
import fr.gouv.recherche.scanr.companies.workflow.MessageQueue;
import fr.gouv.recherche.scanr.companies.workflow.service.ErrorHandler;
import fr.gouv.recherche.scanr.companies.workflow.service.PluginService;
import fr.gouv.recherche.scanr.companies.workflow.service.QueueComponent;
import fr.gouv.recherche.scanr.companies.workflow.service.scheduler.ScheduledJobString;
import fr.gouv.recherche.scanr.config.ErrorPatternConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.regex.Pattern;


@Component
public class ErrorRecoverProcess extends QueueComponent implements PluginService<ScheduledJobString, ScheduledJobString> {
    public static final MessageQueue<ScheduledJobString> QUEUE = MessageQueue.get("ERROR_RECOVER", ScheduledJobString.class);
    public static final String PROVIDER = "errors";
    public static final String ID = "recover";

    @Autowired
    private ErrorPatternConfig patternConfig;

    @Autowired
    private ErrorRepository repository;

    @Autowired
    private ErrorHandler handler;

    @Override
    public ScheduledJobString receiveAndReply(ScheduledJobString message) {
        int[] stats = new int[]{0,0};
        repository.streamAll().forEach(errorMessage -> {
            if (match(errorMessage, patternConfig.getRecover())) {
                handler.recover(errorMessage);
                stats[0]++;
            } else if (match(errorMessage, patternConfig.getIgnore())) {
                repository.delete(errorMessage);
                stats[1]++;
            }
        });
        message.status = String.format("Recovered %d messages. Ignored %d messages.", stats[0], stats[1]);
        return message;
    }

    private boolean match(ErrorMessage error, Set<Pattern> patterns) {
        if (patterns.isEmpty()) return false;
        String stackTrace = error.getStackTrace();
        if (stackTrace == null) {
            return false;
        }
        stackTrace = stackTrace.toLowerCase();
        for (Pattern pattern : patterns) {
            if (pattern.matcher(stackTrace).find()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public MessageQueue<ScheduledJobString> getQueue() {
        return QUEUE;
    }
}
