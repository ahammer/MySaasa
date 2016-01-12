package com.mysassa.core.splash;

import com.mysassa.core.hosting.panels.HostingSidebar;
import com.mysassa.core.splash.panels.SplashContent;
import com.mysassa.core.AbstractModule;

import com.mysassa.core.users.model.User;
import com.mysassa.interfaces.IClassPanelAdapter;
import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.Map;

/**
 * Created by Adam on 12/14/2014.
 */
public class SplashModule extends AbstractModule {
	@Override
	public String getMenuTitle() {
		return null;
	}

	@Override
	public Component getMenuDropDownPanel(String id, IModel model) {
		return null;
	}

	@Override
	public Component getSidebarPanel(String id, IModel model) {
		return new HostingSidebar(id);
	}

	@Override
	public Component getMainPanel(String id, IModel model) {
		return new SplashContent(id, model);
	}

	@Override
	public boolean hasAccess(User.AccessLevel UserAccessLevel) {
		return true;
	}

	@Override
	public Model getDefaultModel() {
		return null;
	}

	@Override
	public Map<Class, IClassPanelAdapter> getClassPanelAdapters() {
		return null;
	}
}
