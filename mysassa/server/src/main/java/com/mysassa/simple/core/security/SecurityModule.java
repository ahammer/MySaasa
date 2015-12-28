package com.mysassa.simple.core.security;

import com.mysassa.simple.core.users.model.User;
import com.mysassa.simple.interfaces.IClassPanelAdapter;
import org.apache.wicket.Component;

import com.mysassa.simple.core.AbstractModule;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.Collections;
import java.util.Map;

public class SecurityModule extends AbstractModule {
	private static final long serialVersionUID = 1L;

	@Override
	public boolean hasAccess(User.AccessLevel UserAccessLevel) {
		return false;
	}

	@Override
	public Model getDefaultModel() {
		return new Model("Not Implemented");
	}

	@Override
	public Map<Class, IClassPanelAdapter> getClassPanelAdapters() {
		return Collections.EMPTY_MAP;
	}

	@Override
	public Component getMenuDropDownPanel(String id, IModel model) {
		return new Label(id, new Model("Disabled"));
	}

	@Override
	public Component getSidebarPanel(String id, IModel model) {
		return new Label(id, new Model("Disabled"));
	}

	@Override
	public Component getMainPanel(String id, IModel model) {
		return new Label(id, model);

	}

	@Override
	public String getMenuTitle() {
		return null;
	}

}
