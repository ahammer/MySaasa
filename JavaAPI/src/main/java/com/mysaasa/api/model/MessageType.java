package com.mysaasa.api.model;

import com.google.gson.JsonObject;

/**
 * If the message has MetaData, it can be handled here
 *
* Created by Adam on 2/18/2015.
*/
public enum MessageType {Reply, Unknown;

    public Object parse(JsonObject jo) {
        switch (this) {
            case Reply:
                return new ReplyMessage(jo.get("comment_id").getAsLong(),jo.get("blogpost_id").getAsLong());
            default:
                break;
        }
        return null;
    }
}
