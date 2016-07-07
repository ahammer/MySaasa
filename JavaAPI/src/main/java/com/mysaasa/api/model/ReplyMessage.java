package com.mysaasa.api.model;

import com.mysaasa.api.messages.IActionableMessage;

/**
 * A Message has come in and it has Json Metadata that make it a Reply message
 *
 * This allows users to find their messages in Blogs/Forums
 *
* Created by Adam on 2/18/2015.
*/
public class ReplyMessage implements IActionableMessage {
    public final long comment_id;
    public final long blogpost_id;

    public ReplyMessage(long comment_id, long blogpost_id) {
        this.comment_id = comment_id;
        this.blogpost_id = blogpost_id;
    }
}
