package com.mysaasa.core.users.panels;

import com.mysaasa.SimpleImpl;
import com.mysaasa.core.users.model.User;
import com.mysaasa.messages.EditContentMessage;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

public class Header extends Panel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4674331521865404519L;

	public Header(String id) {
		super(id);
		add(new AjaxLink("newUser") {
			@Override
			public void onClick(final AjaxRequestTarget target) {
				target.getPage().send(SimpleImpl.getInstance(), Broadcast.BREADTH, new EditContentMessage(new Model(new User()), target));
			}
		});
	}

}
