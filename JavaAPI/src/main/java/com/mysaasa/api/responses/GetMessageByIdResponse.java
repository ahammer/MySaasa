package com.mysaasa.api.responses;

import com.mysaasa.api.model.Message;

/**
 * Created by Adam on 5/30/2016.
 */
public class GetMessageByIdResponse extends SimpleResponse{
    Message data;

    public Message getData() {
        return data;
    }
}
