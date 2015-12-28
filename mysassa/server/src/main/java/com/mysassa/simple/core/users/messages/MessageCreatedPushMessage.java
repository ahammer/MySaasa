package com.mysassa.simple.core.users.messages;

import com.mysassa.simple.core.messaging.model.Message;
import com.mysassa.simple.messages.SimpleWebSocketPushMessage;

/**
 * Created by adam on 15-02-12.
 */
public class MessageCreatedPushMessage extends SimpleWebSocketPushMessage {
	public final Message message;

	public MessageCreatedPushMessage(Message m) {
		this.message = m;
	}

	@Override
	public String getPushMessage() {
		return "MessageCreatedPushMessage";
	}
}
