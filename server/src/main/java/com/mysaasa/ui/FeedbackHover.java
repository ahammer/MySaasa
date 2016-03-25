package com.mysaasa.ui;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * This is a Panel that shows errors and allows self-hiding
 * It's meant for modal-dialog forms
 *
 * Created by adam on 1/5/14.
 */
public class FeedbackHover extends Panel {
	private final AjaxLink closeLink;

	public FeedbackHover(String id) {
		super(id);
		setOutputMarkupId(true);
		setOutputMarkupPlaceholderTag(true);
		setVisible(false);
		FeedbackPanel feedback;
		add(feedback = new FeedbackPanel("feedback"));
		feedback.setOutputMarkupId(true);
		feedback.setOutputMarkupPlaceholderTag(true);
		add(closeLink = new AjaxLink("closeLink") {
			@Override
			public void onClick(AjaxRequestTarget target) {
				hide(target);
			}
		});
	}

	public void show(AjaxRequestTarget target) {
		setVisible(true);
		target.add(this);
	}

	void hide(AjaxRequestTarget target) {
		setVisible(false);
		target.add(this);
	}

	public AjaxLink getCloseLink() {
		return closeLink;
	}
}
