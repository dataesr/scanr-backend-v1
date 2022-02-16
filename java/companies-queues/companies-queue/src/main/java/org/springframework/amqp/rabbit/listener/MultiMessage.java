/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package org.springframework.amqp.rabbit.listener;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;

import java.util.List;

public class MultiMessage extends Message {
    private List<Message> messages;

    public MultiMessage(List<Message> messages) {
        super(null, new MessageProperties());
        Integer priority = messages.stream().map(it -> it.getMessageProperties().getPriority()).filter(it -> it != null).max(Integer::max).orElse(0);
        getMessageProperties().setPriority(priority);
        this.messages = messages;
    }

    public List<Message> getMessages() {
        return messages;
    }
}
