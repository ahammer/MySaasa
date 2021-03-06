package com.mysaasa.core.messaging.panels;

import com.mysaasa.messages.SimpleWebSocketPushMessage;
import com.mysaasa.core.messaging.model.Message;

import java.util.Collections;
import java.util.Map;

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

	@Override
	public Map<String, String> getData() {
		return Collections.emptyMap();
	}
}
