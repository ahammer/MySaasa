package com.mysaasa.messages;

import org.apache.wicket.protocol.ws.api.message.IWebSocketPushMessage;

/**
 * Created by adam on 15-02-15.
 */
public abstract class SimpleWebSocketPushMessage implements IWebSocketPushMessage {
	public abstract String getPushMessage();
}
