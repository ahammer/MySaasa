package com.mysassa.simple.core.users;

import com.mysassa.simple.core.users.data.UserFormData;
import com.mysassa.simple.core.users.model.User;

import com.mysassa.simple.core.users.panels.UserAdmin;
import com.mysassa.simple.core.users.panels.editor.UserEditor;
import com.mysassa.simple.interfaces.AbstractClassPanelAdapter;
import com.mysassa.simple.interfaces.IClassPanelAdapter;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import com.mysassa.simple.core.AbstractModule;

import java.util.HashMap;
import java.util.Map;

public class UsersModule extends AbstractModule {
	private static final long serialVersionUID = 1L;

	@Override
	public boolean hasAccess(User.AccessLevel UserAccessLevel) {
		switch (UserAccessLevel) {
		case ROOT:
			return true;
		case ORG:

		default:
			return false;
		}
	}

	@Override
	public Model getDefaultModel() {
		return new Model(new User());
	}

	@Override
	public Map<Class, IClassPanelAdapter> getClassPanelAdapters() {
		HashMap<Class, IClassPanelAdapter> result = new HashMap();
		result.put(User.class, new AbstractClassPanelAdapter<User>() {
			@Override
			public Panel getEditPanel(String id, User o) {
				return new UserEditor(id, new CompoundPropertyModel<UserFormData>(new UserFormData(o)));
			}
		});
		return result;
	}

	@Override
	public Component getMenuDropDownPanel(String id, IModel model) {
		return null;
	}

	@Override
	public Component getSidebarPanel(String id, IModel model) {
		return new UserAdmin(id);
	}

	@Override
	public Component getMainPanel(String id, IModel model) {
		if (model == null)
			model = getDefaultModel();
		return new UserEditor(id, new CompoundPropertyModel<UserFormData>(new UserFormData((User) model.getObject())));
	}

	@Override
	public String getMenuTitle() {
		return "User";
	}

}
