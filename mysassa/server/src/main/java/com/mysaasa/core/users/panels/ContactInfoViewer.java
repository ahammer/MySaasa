package com.mysaasa.core.users.panels;

import com.mysaasa.core.users.model.ContactInfo;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

public class ContactInfoViewer extends Panel {

	/**
	 *
	 */
	private static final long serialVersionUID = -6389208660285577169L;

	public ContactInfoViewer(String id, IModel<ContactInfo> model) {
		super(id, model);
		if (model.getObject() != null) {
			add(new Label("addressSummary", model.getObject().toString()));
		} else {
			add(new Label("addressSummary", "---"));
		}

	}

}
