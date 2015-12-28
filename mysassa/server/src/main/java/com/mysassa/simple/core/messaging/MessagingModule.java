package com.mysassa.simple.core.messaging;

import com.mysassa.simple.core.AbstractModule;
import com.mysassa.simple.core.messaging.model.Message;
import com.mysassa.simple.core.messaging.panels.MessagePanel;
import com.mysassa.simple.core.messaging.panels.editor.Inbox;
import com.mysassa.simple.core.security.services.session.SecurityContext;
import com.mysassa.simple.core.users.model.User;
import com.mysassa.simple.interfaces.AbstractClassPanelAdapter;
import com.mysassa.simple.interfaces.IClassPanelAdapter;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Adam on 3/27/2015.
 */
public class MessagingModule extends AbstractModule {
	@Override
	public String getMenuTitle() {
		return null;
	}

	@Override
	public Component getMenuDropDownPanel(String id, IModel model) {
		return null;
	}

	@Override
	public Component getSidebarPanel(String id, IModel model) {
		return new Inbox(id, model);
	}

	@Override
	public Component getMainPanel(String id, IModel model) {
		return new MessagePanel(id, new Message());
	}

	@Override
	public boolean hasAccess(User.AccessLevel UserAccessLevel) {
		return true;
	}

	@Override
	public Model getDefaultModel() {
		return new Model(SecurityContext.get().getUser());
	}

	@Override
	public Map<Class, IClassPanelAdapter> getClassPanelAdapters() {
		HashMap<Class, IClassPanelAdapter> result = new HashMap();
		result.put(Message.class, new AbstractClassPanelAdapter<Message>() {
			@Override
			public Panel getEditPanel(String id, Message o) {
				return new MessagePanel(id, o);
			}
		});
		return result;
	}
}
