package com.mysaasa.api;

import com.mysaasa.api.model.Message;
import com.mysaasa.api.model.User;

import java.util.List;

/**
 * Implement this on your platform of choice to store messages
 *
 * Created by Adam on 4/11/2016.
 */
public interface MySaasaMessageStorage {
    Message getMessageById(long id);

    List<Message> getRootMessages(User user);
    List<Message> getMessageThread(Message head);

    void storeMessage(Message m);
    void storeMessages(List<Message> data);
}

