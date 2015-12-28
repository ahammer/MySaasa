package com.mysassa.simple.core.hosting.panels.manager;

import com.mysassa.simple.core.hosting.service.HostingService;
import com.mysassa.simple.core.website.model.Domain;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.form.Form;

import com.mysassa.simple.core.hosting.messages.WebsiteDataChanged.WebsiteCreated;
import com.mysassa.simple.core.website.model.Website;

import java.util.List;

final class SubmitLink extends AjaxSubmitLink {
	/**
	 *
	 */
	private final HostingManagement hostingManagement;
	private static final long serialVersionUID = 1L;

	SubmitLink(HostingManagement hostingManagement) {
		super("submitLink");
		this.hostingManagement = hostingManagement;
	}

	@Override
	public void onError(AjaxRequestTarget target, Form<?> form) {
		target.add(hostingManagement.getForm().feedback);
	}

	@Override
	public void onSubmit(final AjaxRequestTarget target, Form<?> form) {
		final Object modObj = form.getModelObject();
		if (modObj instanceof Website) {
			final Website w = ((Website) modObj);
			List<Domain> domains = HostingService.get().createDomains(hostingManagement.getForm().domains);
			w.setDomains(domains);
			Website w2 = HostingService.get().saveWebsite(w);
			new WebsiteCreated(w, target).send();
			info("Success");

			//MessageHelpers.loadWebsiteEditor(target,w2);
		}

	}
}
