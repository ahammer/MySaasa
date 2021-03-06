package com.mysaasa.core.hosting.panels.manager;

import com.mysaasa.core.hosting.service.HostingService;
import com.mysaasa.core.website.model.Domain;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.form.Form;

import com.mysaasa.core.hosting.messages.WebsiteDataChanged.WebsiteCreated;
import com.mysaasa.core.website.model.Website;

import java.util.List;

import static com.mysaasa.MySaasa.getService;

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
			HostingService service = getService(HostingService.class);
			List<Domain> domains = service.createDomains(hostingManagement.getForm().domains);
			w.setDomains(domains);
			Website w2 = service.saveWebsite(w);
			new WebsiteCreated(w, target).send();
			info("Success");

			// MessageHelpers.loadWebsiteEditor(target,w2);
		}

	}
}
