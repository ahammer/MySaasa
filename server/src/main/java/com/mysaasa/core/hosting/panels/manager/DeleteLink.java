package com.mysaasa.core.hosting.panels.manager;

import com.mysaasa.SimpleImpl;
import com.mysaasa.core.hosting.service.HostingService;
import com.mysaasa.messages.MessageHelpers;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;

import com.mysaasa.core.hosting.messages.WebsiteDataChanged.WebsiteDeleted;
import com.mysaasa.core.website.model.Website;
import org.apache.wicket.model.CompoundPropertyModel;

final class DeleteLink extends AjaxLink {
	private final HostingManagement hostingManagement;
	private static final long serialVersionUID = 1L;

	DeleteLink(HostingManagement hostingManagement) {
		super("deleteLink");
		this.hostingManagement = hostingManagement;
	}

	@Override
	public void onClick(final AjaxRequestTarget target) {
		final Object modObj = hostingManagement.getForm().getModelObject();
		if (modObj instanceof Website) {
			final Website website = (Website) modObj;
			HostingService websiteDataService = SimpleImpl.get().getInjector().getProvider(HostingService.class).get();
			websiteDataService.deleteWebsite(website);
			new WebsiteDeleted(website, target).send();
			MessageHelpers.editEventMessage(target, new CompoundPropertyModel<Website>(new Website()));
		}
	}
}
