package com.mysassa.core.messaging.panels;

import com.mysassa.core.messaging.model.Message;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

/**
 * Created by adam on 15-01-25.
 */
public class MessageRow extends Panel {
	public MessageRow(String id, IModel<Message> model) {
		super(id, model);
		add(new Label("title"));
		add(new Label("senderContactInfo.name"));
		add(new Label("timeSent"));
	}

}
