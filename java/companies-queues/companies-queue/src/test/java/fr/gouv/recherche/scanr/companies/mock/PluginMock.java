/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.companies.mock;

import fr.gouv.recherche.scanr.companies.workflow.MessageQueue;

import java.util.function.Function;

/**
 *
 */
public interface PluginMock<E> extends Function<E, Object> {

    default void fire(E dto, MessageQueue replyTo, MockQueueService mql) {
        Object o = this.apply(dto);
        if(o != null)
            mql.fire(replyTo, o);
    }
}
