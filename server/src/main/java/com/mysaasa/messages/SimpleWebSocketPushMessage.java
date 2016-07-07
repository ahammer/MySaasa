package com.mysaasa.messages;

import org.apache.wicket.protocol.ws.api.message.IWebSocketPushMessage;

import java.util.Map;

/**
 * Created by adam on 15-02-15.
 */
public abstract class SimpleWebSocketPushMessage implements IWebSocketPushMessage {
	public abstract String getPushMessage();

	public abstract Map<String, String> getData();
}
