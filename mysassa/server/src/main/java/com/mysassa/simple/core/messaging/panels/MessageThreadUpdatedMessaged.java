package com.mysassa.simple.core.messaging.panels;

import com.mysassa.simple.core.messaging.model.Message;
import com.mysassa.simple.messages.SimpleWebSocketPushMessage;

/**
 * Created by adam on 15-03-29.
 */
public class MessageThreadUpdatedMessaged extends SimpleWebSocketPushMessage {
	public final Message message;

	public MessageThreadUpdatedMessaged(Message m) {
		super();
		this.message = m;
	}

	@Override
	public String getPushMessage() {
		return "MessageThreadUpdated";
	}
}
