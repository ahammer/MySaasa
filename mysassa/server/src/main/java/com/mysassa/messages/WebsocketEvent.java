package com.mysassa.messages;

import org.apache.wicket.protocol.ws.api.WebSocketRequestHandler;
import org.apache.wicket.protocol.ws.api.message.IWebSocketPushMessage;

/**
 * Created by adam on 15-03-24.
 */
public class WebsocketEvent {
	public final WebSocketRequestHandler handler;
	public final IWebSocketPushMessage message;

	public WebsocketEvent(WebSocketRequestHandler handler, IWebSocketPushMessage message) {
		this.message = message;
		this.handler = handler;
	}
}
