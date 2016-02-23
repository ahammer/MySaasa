package com.mysaasa.core.users.messages;

import com.mysaasa.messages.SimpleWebSocketPushMessage;
import com.mysaasa.core.messaging.model.Message;

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
