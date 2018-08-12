package com.mysaasa.core.hosting;

import com.mysaasa.core.hosting.panels.HostingSidebar;
import com.mysaasa.core.users.model.User;
import com.mysaasa.core.website.model.Website;
import com.mysaasa.interfaces.AbstractClassPanelAdapter;
import com.mysaasa.interfaces.IClassPanelAdapter;
import com.mysaasa.core.hosting.panels.manager.HostingManagement;
import org.apache.wicket.Component;

import com.mysaasa.core.AbstractModule;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.HashMap;
import java.util.Map;

public class HostingModule extends AbstractModule {
	private static final long serialVersionUID = 1L;

	@Override
	public boolean hasAccess(User.AccessLevel UserAccessLevel) {
		if (UserAccessLevel == null)
			throw new IllegalStateException("No user level");
		switch (UserAccessLevel) {
		case ROOT:
		case WWW:
		case ORG:
			return true;
		default:
			return false;
		}
	}

	@Override
	public Model getDefaultModel() {
		return new Model(new Website());
	}

	@Override
	public Map<Class, IClassPanelAdapter> getClassPanelAdapters() {

		HashMap<Class, IClassPanelAdapter> result = new HashMap();

		result.put(Website.class, new AbstractClassPanelAdapter<Website>() {
			@Override
			public Panel getEditPanel(String id, Website o) {
				return new HostingManagement(id, new CompoundPropertyModel<Website>(o));
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
		return new HostingSidebar(id);
	}

	@Override
	public Component getMainPanel(String id, IModel model) {
		return new HostingManagement(id, new CompoundPropertyModel<Website>(new Website()));
	}

	@Override
	public String getMenuTitle() {
		return "Hosting";
	}

	/**
	 * public static class CreateHostingEditModalWindow extends CreateModalWindow {
	 * 
	 * private final ManageWebsiteRegistration rum; private AjaxRequestTarget target;
	 * 
	 * public CreateHostingEditModalWindow(ManageWebsiteRegistration rum, AjaxRequestTarget target) { this.rum = rum; this.target = target; }
	 * 
	 * @Override public void initialize(final ModalWindow window) { window.setTitle("Hosting Registry"); window.setContent(new MyHostingManagement(window)); window.setInitialWidth(400); window.setAutoSize(true); window.show(target); target = null; }
	 * 
	 * @Override public AjaxRequestTarget getAjaxRequestTarget() { return target; }
	 * 
	 *           private class MyHostingManagement extends HostingManagement {
	 * 
	 *           private final ModalWindow window;
	 * 
	 *           public MyHostingManagement(ModalWindow window) { super(window.getContentId(), new CompoundPropertyModel<>(CreateHostingEditModalWindow.this.rum.getWebsite())); this.window = window; }
	 * 
	 * @Override public void done(AjaxRequestTarget target) { window.close(target); } } }
	 */
}
