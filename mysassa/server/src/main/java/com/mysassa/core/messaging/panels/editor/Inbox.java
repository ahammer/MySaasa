package com.mysassa.core.messaging.panels.editor;

import com.mysassa.core.messaging.panels.MessagesDataProvider;
import com.mysassa.core.messaging.panels.MessagesDataTable;
import com.mysassa.core.users.model.User;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

public class Inbox extends Panel {
	private static final long serialVersionUID = 1L;

	public Inbox(String id, final IModel<User> model) {
		super(id, model);
		setOutputMarkupId(true);
		add(new MessagesDataTable("messagesDataTable", model.getObject(), new MessagesDataProvider(model.getObject())));

	}

}
