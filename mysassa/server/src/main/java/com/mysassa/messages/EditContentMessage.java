package com.mysassa.messages;

import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;

/**
 * When we want to edit some content, this is used to communicate with the EditorContent panel
 *
 * Created by adam on 2014-10-12.
 */
public class EditContentMessage extends AjaxIntent {

	private final IModel model;

	public EditContentMessage(IModel model, AjaxRequestTarget target) {
		super(target);
		this.setAction("/Edit/" + model.getObject().getClass().getSimpleName());
		this.model = model;
	}

	public EditContentMessage(IModel model, Page target) {
		super(target);
		this.setAction("/Edit/" + model.getObject().getClass().getSimpleName());
		this.model = model;
	}

	public IModel getModel() {
		return model;
	}

}
