/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.companies.workflow.service;

import fr.gouv.recherche.scanr.companies.model.error.ErrorMessage;
import fr.gouv.recherche.scanr.companies.workflow.dto.PluginError;
import fr.gouv.recherche.scanr.companies.workflow.MessageQueue;

/**
 * Public interface to the workflow error handler
 */
public interface ErrorHandler {


    boolean recover(ErrorMessage message);

    /**
     * Handle a user-generated error object
     *
     * @param error the object
     */
    void handle(ErrorMessage error);
}
