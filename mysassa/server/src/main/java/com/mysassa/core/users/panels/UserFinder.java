package com.mysassa.core.users.panels;

import com.mysassa.core.users.model.User;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.panel.Panel;

public abstract class UserFinder extends Panel {
	private static final long serialVersionUID = -917007546017624998L;
	private final int requestCode;

	public UserFinder(String id, int RequestCode) {
		super(id);
		requestCode = RequestCode;
		UsersDataTable table;
		UsersDataProvider provider;
		add(table = new UsersDataTable("userList", provider = new UsersDataProvider()) {
			@Override
			public void onRowClick(AjaxRequestTarget target, User u) {
				onSelection(target, u, requestCode);
			}
		});
	}

	public abstract void onSelection(AjaxRequestTarget target, User u, int requestCode);
}
