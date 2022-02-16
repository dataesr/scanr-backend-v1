/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.api;

import fr.gouv.recherche.scanr.api.exception.NotFoundException;
import fr.gouv.recherche.scanr.api.util.ApiConstants;
import fr.gouv.recherche.scanr.companies.model.scheduler.TriggerInfo;
import fr.gouv.recherche.scanr.companies.workflow.service.scheduler.QueueScheduler;
import fr.gouv.recherche.scanr.workflow.errors.ErrorRecoverProcess;
import fr.gouv.recherche.scanr.workflow.menesr.MenesrFetchProcess;
import fr.gouv.recherche.scanr.workflow.menesr.RecrawlProcess;
import fr.gouv.recherche.scanr.workflow.search.IndexUpdatedProcess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

/**
 *
 */
@Controller
@RequestMapping("/services/scheduling")
public class SchedulingApi {
    @Autowired
    private QueueScheduler queueScheduler;

    @ResponseBody
    @RequestMapping(value = "/{id}/schedule", method = RequestMethod.POST, produces = "application/json; charset=UTF-8")
    public ApiConstants.OK schedule(@PathVariable("id") String id) {
        // CRON expression : see https://riptutorial.com/spring/example/21209/cron-expression
        // second, minute, hour, day of month, month, day(s) of week
        switch (id) {
            case MenesrFetchProcess.PROVIDER + ":" + MenesrFetchProcess.ID_SPPP:
                queueScheduler.scheduleJob(MenesrFetchProcess.PROVIDER, MenesrFetchProcess.ID_SPPP, MenesrFetchProcess.FetchType.STRUCTURE_PUBLICATION_PROJECT_PERSON, new TriggerInfo("0 0 8 5 * ?"), MenesrFetchProcess.QUEUE, null, 0);
                break;
            case IndexUpdatedProcess.PROVIDER + ":" + IndexUpdatedProcess.ID:
                queueScheduler.scheduleJob(IndexUpdatedProcess.PROVIDER, IndexUpdatedProcess.ID, IndexUpdatedProcess.ID, new TriggerInfo("0 0 1 6 * ?"), IndexUpdatedProcess.QUEUE, null, 0);
                break;
            case ErrorRecoverProcess.PROVIDER + ":" + ErrorRecoverProcess.ID:
                queueScheduler.scheduleJob(ErrorRecoverProcess.PROVIDER, ErrorRecoverProcess.ID, ErrorRecoverProcess.ID, new TriggerInfo("0 0 23 6 * ?"), ErrorRecoverProcess.QUEUE, null, 0);
                break;
            case RecrawlProcess.PROVIDER + ":" + RecrawlProcess.ID:
                queueScheduler.scheduleJob(RecrawlProcess.PROVIDER, RecrawlProcess.ID, RecrawlProcess.ID, new TriggerInfo("0 0 23 1,15 * ?"), RecrawlProcess.QUEUE, null, 0);
                break;
            default:
                throw new NotFoundException("scheduling", id);

        }
        return ApiConstants.OK_MESSAGE;
    }


    @ResponseBody
    @RequestMapping(value = "/{id}/now", method = RequestMethod.POST, produces = "application/json; charset=UTF-8")
    public ApiConstants.OK scheduleNow(@PathVariable("id") String id) {
        queueScheduler.adjustNextExecution(id, new Date());
        return ApiConstants.OK_MESSAGE;
    }

    @ResponseBody
    @RequestMapping(value = "/{id}/unschedule", method = RequestMethod.POST, produces = "application/json; charset=UTF-8")
    public ApiConstants.OK unschedule(@PathVariable("id") String id) {
        queueScheduler.cancelJob(id);
        return ApiConstants.OK_MESSAGE;
    }

}
