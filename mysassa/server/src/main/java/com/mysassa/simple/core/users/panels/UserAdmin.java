package com.mysassa.simple.core.users.panels;

import com.mysassa.simple.messages.MessageHelpers;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.panel.Panel;

import com.mysassa.simple.core.users.model.User;
import org.apache.wicket.model.Model;

public class UserAdmin extends Panel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6881467492971355182L;

	public UserAdmin(String id) {
		super(id);
		UsersDataProvider provider;
		UsersDataTable table;
		add(table = new UsersDataTable("UsersDataTable", provider = new UsersDataProvider()) {
			@Override
			public void onRowClick(final AjaxRequestTarget target, User u) {
				MessageHelpers.editEventMessage(target, new Model(u));
			}
		});
	}

}
