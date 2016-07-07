package com.mysaasa.api;

import com.mysaasa.api.messages.NewMessageEvent;
import com.mysaasa.api.model.Message;
import com.mysaasa.api.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Default in-memory implementation of the interface
 */
public class InMemoryMessageStorage implements MySaasaMessageStorage{
    final MySaasaClient client;
    Map<User,    List<Message>> rootMessageMap   = new HashMap<>();
    Map<com.mysaasa.api.model.Message, List<com.mysaasa.api.model.Message>> threadMessageMap = new HashMap<>();
    Map<Long, com.mysaasa.api.model.Message>       idLookupMap      = new HashMap<>();

    public InMemoryMessageStorage(MySaasaClient manager) {
        this.client = manager;
    }

    @Override
    public com.mysaasa.api.model.Message getMessageById(long id) {
        return idLookupMap.get(id);
    }

    @Override
    public List<com.mysaasa.api.model.Message> getRootMessages(User user) {
        return rootMessageMap.get(user);
    }

    @Override
    public List<com.mysaasa.api.model.Message> getMessageThread(com.mysaasa.api.model.Message head) {
        if (head.messageThreadRoot != null) {
            return threadMessageMap.get(threadMessageMap);
        }
        return threadMessageMap.get(head);
    }

    @Override
    public void storeMessage(com.mysaasa.api.model.Message m) {
        putInIdLookupMap(m);
        putInThreadMessageMap(m);
        putInRootMessageMap(m);
    }

    private void putInRootMessageMap(com.mysaasa.api.model.Message m) {
        if (m.messageThreadRoot != null) return;
        //Get list and initialize if necessary
        User user = client.getAuthenticationManager().getAuthenticatedUser();
        List<com.mysaasa.api.model.Message> messages = rootMessageMap.get(user);
        if (messages == null) {
            messages = new ArrayList<>();
        }

        //Add It to list if it doesn't exist
        if (!messages.contains(m))
            messages.add(m);

        rootMessageMap.put(user, messages);
    }

    private void putInThreadMessageMap(com.mysaasa.api.model.Message m) {
        //Find the root message
        com.mysaasa.api.model.Message root = null;
        if (m.messageThreadRoot != null) {
            root = m.messageThreadRoot;
        } else {
            root = m;
        }


        //Get list and initialize if necessary
        List<com.mysaasa.api.model.Message> messages = threadMessageMap.get(root);
        if (messages == null) {
            messages = new ArrayList<>();
        }

        //Add It to list if it doesn't exist
        if (!messages.contains(m))
            messages.add(m);
        threadMessageMap.put(root, messages);
    }

    private void putInIdLookupMap(com.mysaasa.api.model.Message m) {
        if (!idLookupMap.containsKey(m)) {
            client.bus.post(new NewMessageEvent(m));
        }
        idLookupMap.put(m.id, m);
    }

    @Override
    public void storeMessages(List<com.mysaasa.api.model.Message> data) {
        for (com.mysaasa.api.model.Message m:data) storeMessage(m);
    }
}
