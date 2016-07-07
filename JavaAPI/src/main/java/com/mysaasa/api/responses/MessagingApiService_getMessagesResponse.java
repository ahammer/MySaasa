package com.mysaasa.api.responses;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by adam on 2014-10-16.
 */
public class MessagingApiService_getMessagesResponse extends SimpleResponse {
    public List<com.mysaasa.api.model.Message> messages = new ArrayList();
}
