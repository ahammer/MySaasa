package com.mysassa.core.website.panels;

import com.mysassa.core.security.services.session.AdminSession;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import java.io.File;

/**
 * Created by adam on 15-02-04.
 */
public abstract class DeleteFileConfirmation extends Panel {
	public DeleteFileConfirmation(String id, IModel<File> model) {
		super(id, model);
		add(new Label("label", model.getObject().getName() + " in " + AdminSession.get().getEnv()));
		add(new AjaxLink("yes") {
			@Override
			public void onClick(AjaxRequestTarget target) {
				yes(target);
			}
		});
		add(new AjaxLink("no") {
			@Override
			public void onClick(AjaxRequestTarget target) {
				no(target);
			}
		});
	}

	protected abstract void yes(AjaxRequestTarget target);

	protected abstract void no(AjaxRequestTarget target);
}
