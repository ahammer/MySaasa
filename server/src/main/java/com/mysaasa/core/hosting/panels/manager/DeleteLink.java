package com.mysaasa.core.hosting.panels.manager;

import com.mysaasa.MySaasa;
import com.mysaasa.core.hosting.service.HostingService;
import com.mysaasa.messages.MessageHelpers;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;

import com.mysaasa.core.hosting.messages.WebsiteDataChanged.WebsiteDeleted;
import com.mysaasa.core.website.model.Website;
import org.apache.wicket.model.CompoundPropertyModel;

import javax.inject.Inject;

import static com.mysaasa.MySaasa.inject;

final class DeleteLink extends AjaxLink {
	private final HostingManagement hostingManagement;
	private static final long serialVersionUID = 1L;

	@Inject
	HostingService hostingService;

	DeleteLink(HostingManagement hostingManagement) {
		super("deleteLink");
		this.hostingManagement = hostingManagement;
		inject(this);
	}

	@Override
	public void onClick(final AjaxRequestTarget target) {
		final Object modObj = hostingManagement.getForm().getModelObject();
		if (modObj instanceof Website) {
			final Website website = (Website) modObj;
			hostingService.deleteWebsite(website);
			new WebsiteDeleted(website, target).send();
			MessageHelpers.editEventMessage(target, new CompoundPropertyModel<Website>(new Website()));
		}
	}
}
