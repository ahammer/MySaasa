package com.mysaasa.core.users.panels;

import java.util.Iterator;
import java.util.List;

;
import com.mysaasa.core.users.model.User;

import com.mysaasa.core.users.service.UserService;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

public class UsersDataProvider extends SortableDataProvider<User, String> {
	private static final long serialVersionUID = 1L;

	public UsersDataProvider() {}

	@Override
	public Iterator<? extends User> iterator(long first, long count) {

		List<User> userlist = UserService.get().getAllUsers();
		if (userlist.size() <= count) return userlist.iterator();
		final Iterator<? extends User> result = userlist.subList((int) first, (int) (first + count)).iterator();
		return result;
	}

	@Override
	public IModel<User> model(User object) {
		return new CompoundPropertyModel(object);
	}

	@Override
	public long size() {
		return UserService.get().getAllUsers().size();
	}
}
