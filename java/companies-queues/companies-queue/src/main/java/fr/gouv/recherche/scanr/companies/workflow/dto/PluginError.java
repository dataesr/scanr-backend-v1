/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.companies.workflow.dto;

import fr.gouv.recherche.scanr.companies.workflow.MessageQueue;

/**
*
*/
public class PluginError {
    public String error;
    public MessageQueue reply_to;
    public String original_message;
    public MessageQueue queue;
}
