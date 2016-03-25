package com.mysaasa.core.website;

import com.mysaasa.interfaces.AbstractClassPanelAdapter;
import com.mysaasa.core.users.model.User;
import com.mysaasa.core.website.messages.WebsiteCreateNewFile;
import com.mysaasa.core.website.model.TemplateFile;
import com.mysaasa.core.website.model.Website;
import com.mysaasa.core.website.panels.WebViewer;
import com.mysaasa.core.website.panels.WebsiteControls;
import com.mysaasa.core.website.panels.WebsiteSidebar;
import com.mysaasa.interfaces.IClassPanelAdapter;
import org.apache.wicket.Component;

import com.mysaasa.core.AbstractModule;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.HashMap;
import java.util.Map;

public class WebsiteModule extends AbstractModule {
	@Override
	public boolean hasAccess(User.AccessLevel UserAccessLevel) {
		switch (UserAccessLevel) {
		case ORG:
		case WWW:
		case ROOT:
			return true;
		default:
			return false;
		}
	}

	@Override
	public Model getDefaultModel() {
		return new Model("not Implemented");
	}

	private static final long serialVersionUID = 1L;

	public WebsiteModule() {
		super();
	}

	@Override
	public Component getMenuDropDownPanel(String id, IModel model) {
		return new WebsiteControls(id, model);
	}

	@Override
	public Component getSidebarPanel(String id, IModel model) {
		Website website = (Website) model.getObject();
		return new WebsiteSidebar(id, model, new Model(website.calculateDefaultFile()));
	}

	@Override
	public Component getMainPanel(String id, IModel model) {
		if (model.getObject() instanceof Website) {
			try {
				TemplateFile templateFileModel = ((Website) model.getObject()).calculateDefaultFile();
				return new WebViewer(id, new Model(templateFileModel));
			} catch (Exception e) {
				e.printStackTrace();
				;
				return new Label(id, model.getObject().getClass() + " Not setup yet " + e.getMessage());
			}
		}
		return new Label(id, model);
	}

	//This is null because I don't want it to show.
	@Override
	public String getMenuTitle() {
		return null;
	}

	@Override
	public void onEvent(IEvent<?> event) {
		super.onEvent(event);
		if (event.getPayload() instanceof WebsiteCreateNewFile) {
			WebsiteCreateNewFile wcnf = (WebsiteCreateNewFile) event.getPayload();
			throw new RuntimeException("Not Implemented");

		}
	}

	@Override
	public Map<Class, IClassPanelAdapter> getClassPanelAdapters() {
		HashMap<Class, IClassPanelAdapter> result = new HashMap();
		result.put(TemplateFile.class, new AbstractClassPanelAdapter() {
			@Override
			public Panel getEditPanel(String id, Object o) {
				return new WebViewer(id, new Model((TemplateFile) o));
			}
		});
		return result;
	}

}
