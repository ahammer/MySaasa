package com.mysassa.simple.core.hosting.panels;

import com.mysassa.simple.core.website.model.Domain;
import com.mysassa.simple.core.website.model.Website;
import com.mysassa.simple.messages.MessageHelpers;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 * Created by Adam on 1/24/2015.
 */
public class HostingRow extends Panel {
	public HostingRow(String id, IModel<Website> model) {
		super(id, model);
		add(new Label("production"));

		add(new Label("isVisible"));
		add(new Label("organization"));
		WebMarkupContainer aka_label;
		add(aka_label = new WebMarkupContainer("aka_label"));
		RepeatingView repeater;
		add(repeater = new RepeatingView("aka"));
		Website w = model.getObject();

		if (w == null || w.getDomains() == null || w.getDomains().size() == 0) {
			repeater.setVisible(false);
			aka_label.setVisible(false);
		} else {
			for (Domain d : w.getDomains()) {
				repeater.add(new Label(repeater.newChildId(), new Model(d.domain)));
			}
		}

		add(new AjaxLink("website") {
			@Override
			public void onClick(AjaxRequestTarget target) {
				MessageHelpers.loadWebsiteEditor(target, (Website) HostingRow.this.getDefaultModelObject());

			}
		});
		add(new AjaxLink("hosting") {
			@Override
			public void onClick(AjaxRequestTarget target) {
				MessageHelpers.editEventMessage(target, HostingRow.this.getDefaultModel());
			}
		});

	}
}
