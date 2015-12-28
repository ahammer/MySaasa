package com.mysassa.simple.core.organization.panels;

import com.mysassa.simple.core.organization.model.Organization;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

/**
 * Created by adam on 15-01-25.
 */
public class OrganizationRow extends Panel {
	public OrganizationRow(String id, IModel<Organization> model) {
		super(id, model);
		add(new Label("name"));
	}
}
