package com.mysaasa.api.model;

/**
 * When a Push Message (GCM or Websocket) comes through saying their is a new Message (MySaasa domain)
 * The model is in this format
 */
public class PushMessageModel {
    Message message;

    public Message getMessage() {
        return message;
    }
}
