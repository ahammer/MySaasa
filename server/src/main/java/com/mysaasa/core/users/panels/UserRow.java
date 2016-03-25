package com.mysaasa.core.users.panels;

import com.mysaasa.core.users.model.User;
import com.mysaasa.messages.DataUpdateEvent;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

/**
 * Created by adam on 15-01-25.
 */
public class UserRow extends Panel {
	public UserRow(String id, IModel<User> model) {
		super(id, model);
		setOutputMarkupId(true);
		add(new Label("identifier"));
		add(new Label("organization"));
	}

	@Override
	public void onEvent(IEvent<?> event) {
		super.onEvent(event);
		if (event.getPayload() instanceof DataUpdateEvent) {
			DataUpdateEvent evt = (DataUpdateEvent) event.getPayload();
			if (evt.obj instanceof User) {
				User u = (User) evt.obj;
				if (u.id == ((User) getDefaultModelObject()).id) {
					setDefaultModelObject(u);
					evt.getAjaxRequestTarget().add(this);
				}

			}
		}
	}

}
