package com.mysaasa.core.users.messages;

import com.mysaasa.core.users.model.User;
import com.mysaasa.messages.AjaxMessage;

@Deprecated // move to AjaxIntent
public abstract class UserDataChanged extends AjaxMessage {

	public abstract static class UserDeleted extends UserDataChanged {
		public UserDeleted(User u) {
			super(u);
		}
	}

	public abstract static class UserUpdated extends UserDataChanged {
		public UserUpdated(User u) {
			super(u);
		}
	}

	private final User user;

	UserDataChanged(User u) {
		super();
		user = u;
	}

}
