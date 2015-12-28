package com.mysassa.simple.core.help;

import com.mysassa.simple.core.AbstractModule;
import com.mysassa.simple.core.help.panels.HelpContent;
import com.mysassa.simple.core.help.panels.HelpSidebarLinks;
import com.mysassa.simple.core.users.model.User;
import com.mysassa.simple.interfaces.IClassPanelAdapter;
import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.Map;

/**
 * Created by Adam on 12/14/2014.
 */
public class HelpModule extends AbstractModule {
	@Override
	public String getMenuTitle() {
		return "Help";
	}

	@Override
	public Component getMenuDropDownPanel(String id, IModel model) {
		return null;
	}

	@Override
	public Component getSidebarPanel(String id, IModel model) {
		return new HelpSidebarLinks(id, model);
	}

	@Override
	public Component getMainPanel(String id, IModel model) {
		return new HelpContent(id, model);
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
