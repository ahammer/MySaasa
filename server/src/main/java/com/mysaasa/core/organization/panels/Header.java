package com.mysaasa.core.organization.panels;

import com.mysaasa.MySaasa;
import com.mysaasa.core.organization.messages.ManageOrganization;
import com.mysaasa.core.organization.model.Organization;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.html.panel.Panel;

public class Header extends Panel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4674331521865404519L;

	public Header(String id) {
		super(id);
		add(new AjaxLink("newUser") {
			@Override
			public void onClick(final AjaxRequestTarget target) {
				target.getPage().send(MySaasa.getInstance(), Broadcast.BREADTH, new ManageOrganization(new Organization()) {

					@Override
					public AjaxRequestTarget getAjaxRequestTarget() {
						return target;
					}
				});
			}

		});
	}

}
