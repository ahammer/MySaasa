package com.mysassa.core.organization;

import com.mysassa.SimpleImpl;
import com.mysassa.core.users.model.User;
import com.mysassa.interfaces.AbstractClassPanelAdapter;
import com.mysassa.interfaces.IClassPanelAdapter;
import com.mysassa.messages.EditContentMessage;
import com.mysassa.core.organization.model.Organization;
import com.mysassa.core.organization.panels.manager.OrganizationManagement;
import com.mysassa.core.organization.messages.ManageOrganization;
import com.mysassa.core.organization.panels.OrganizationAdmin;

import org.apache.wicket.Component;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import com.mysassa.core.AbstractModule;

import java.util.HashMap;
import java.util.Map;

public class OrganizationModule extends AbstractModule {
	private static final long serialVersionUID = 1L;

	@Override
	public Component getMenuDropDownPanel(String id, IModel model) {
		return null;
	}

	@Override
	public Component getSidebarPanel(String id, IModel model) {
		return new OrganizationAdmin(id);
	}

	@Override
	public Component getMainPanel(String id, IModel model) {
		if (model == null)
			model = new Model(new Organization());
		return new OrganizationManagement(id, new CompoundPropertyModel<Organization>((Organization) model.getObject()));
	}

	@Override
	public boolean hasAccess(User.AccessLevel UserAccessLevel) {
		switch (UserAccessLevel) {
		case ROOT:
			return true;
		default:
			return false;
		}
	}

	@Override
	public Model getDefaultModel() {
		return new Model(new Organization());
	}

	@Override
	public String getMenuTitle() {
		return "Organization";
	}

	// We handle the RequestUserMessage.
	@Override
	public void onEvent(IEvent event) {
		if (event.getPayload() instanceof ManageOrganization) {
			final ManageOrganization mu = (ManageOrganization) event.getPayload();
			mu.getAjaxRequestTarget().getPage().send(SimpleImpl.get(), Broadcast.BREADTH, new EditContentMessage(new Model(mu.getOrganization()), mu.getAjaxRequestTarget()));
		}
	}

	@Override
	public Map<Class, IClassPanelAdapter> getClassPanelAdapters() {
		HashMap<Class, IClassPanelAdapter> result = new HashMap();
		result.put(Organization.class, new AbstractClassPanelAdapter<Organization>() {
			@Override
			public Panel getEditPanel(String id, Organization o) {
				if (o == null)
					o = new Organization();
				return new OrganizationManagement(id, new CompoundPropertyModel<Organization>(o));
			}
		});

		return result;
	}

}
