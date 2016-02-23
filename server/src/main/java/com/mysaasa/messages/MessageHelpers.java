package com.mysaasa.messages;

import com.mysaasa.core.website.WebsiteModule;
import com.mysaasa.core.website.model.Website;
import com.mysaasa.Simple;
import com.mysaasa.core.AbstractModule;
import com.mysaasa.core.website.model.TemplateFile;
import com.mysaasa.core.website.panels.details.EXTRAS;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.protocol.ws.api.WebSocketRequestHandler;
import org.apache.wicket.protocol.ws.api.message.IWebSocketPushMessage;

import java.util.logging.Logger;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Let's add some consistency to messaging, this file is for Helpers that help broadcast messages.
 * To reduce some boilerplate
 *
 * Created by adam on 2014-10-13.
 */
public class MessageHelpers {

	/**
	 * This is a helper to request editing, just to reduce a minor amount of boilerplate
	 * @param target
	 * @param defaultModel
	 */
	public static void editEventMessage(AjaxRequestTarget target, IModel<?> defaultModel) {
		checkNotNull(defaultModel);
		checkNotNull(defaultModel.getObject());
		System.out.println("Editing Event Message for " + defaultModel.getObject().toString());
		target.getPage().send(Simple.get(), Broadcast.BREADTH, new EditContentMessage(defaultModel, target));
	}

	public static void editEventMessage(Page page, IModel<?> defaultModel) {
		checkNotNull(defaultModel);
		checkNotNull(defaultModel.getObject());
		System.out.println("Editing Event Message for " + defaultModel.getObject().toString());
		page.send(Simple.get(), Broadcast.BREADTH, new EditContentMessage(defaultModel, page));
	}

	public static void notifyUpdate(AjaxRequestTarget target, Object obj) {
		target.getPage().send(Simple.get(), Broadcast.BREADTH, new DataUpdateEvent(target, obj));
	}

	/**
	 * Sends the message that will load the Editor
	 * @param target
	 * @param website
	 */
	public static void loadWebsiteEditor(final AjaxRequestTarget target, Website website) {
		checkNotNull(website);
		loadWebsiteEditor(target, website, website.calculateDefaultFile());
	}

	public static void loadWebsiteEditor(AjaxRequestTarget target, Website website, TemplateFile file) {
		if (file == null) {
			System.out.println("Error loading website: " + website);
			return;
		}
		checkNotNull(website);
		ModuleClickedMessage mcm = new ModuleClickedMessage(new WebsiteModule(), new Model(website), target);

		mcm.send();
		editEventMessage(target, new Model(file));

	}

	public static void loadWebsiteEditor(Website website, Page page) {
		checkNotNull(website);
		ModuleClickedMessage mcm = new ModuleClickedMessage(new WebsiteModule(), new Model(website), page);
		mcm.send();

	}

	public static void broadcastPushEvent(Page p, WebSocketRequestHandler handler, IWebSocketPushMessage message) {
		checkNotNull(handler);
		checkNotNull(message);
		p.send(Simple.get(), Broadcast.BREADTH, new WebsocketEvent(handler, message));

	}

	public static void gotoModule(AjaxRequestTarget target, Class module) {
		ModuleClickedMessage mcm = null;
		try {
			mcm = new ModuleClickedMessage((AbstractModule) module.newInstance(), target);
			mcm.send();
		} catch (InstantiationException e) {

			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

	}
}
