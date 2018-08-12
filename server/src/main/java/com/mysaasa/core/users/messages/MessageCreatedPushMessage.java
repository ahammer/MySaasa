package com.mysaasa.core.users.messages;

import com.mysaasa.messages.SimpleWebSocketPushMessage;
import com.mysaasa.core.messaging.model.Message;

import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by adam on 15-02-12.
 */
public class MessageCreatedPushMessage extends SimpleWebSocketPushMessage {
	public final Message message;
	private final HashMap<String, String> data;

	public MessageCreatedPushMessage(Message m) {
		checkNotNull(m);
		this.message = m;
		data = new HashMap<>();

		// We want to truncate data for display but not send over the 4k limit
		String title = message.getTitle();
		String body = message.getBody();

		if (title.length() > 50)
			title = title.substring(0, 50);
		if (body.length() > 50)
			body = body.substring(0, 50);

		data.put("id", String.valueOf(message.getId()));
		data.put("title", title);
		data.put("body", body);
		data.put("sender", message.getSender().getIdentifier());
	}

	@Override
	public String getPushMessage() {
		return this.getClass().getSimpleName();
	}

	@Override
	public Map<String, String> getData() {
		return data;
	}
}
