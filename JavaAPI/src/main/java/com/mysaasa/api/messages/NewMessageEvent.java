package com.mysaasa.api.messages;

import com.mysaasa.api.model.Message;

/**
 * Created by adamhammer2 on 2016-04-14.
 */
public class NewMessageEvent {
    private final Message message;

    public NewMessageEvent(Message m) {
        this.message = m;
    }

    public Message getMessage() {
        return message;
    }
}
