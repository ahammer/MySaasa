package com.mysassa.simple.ui.content;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

import static com.google.common.base.Preconditions.checkNotNull;

//TODO switch to model<AbstractModule> instead of inner field and .remove()/.bindTemplateService()
//as per wicket best practices
public class PanelContainer extends Panel {

	private static final long serialVersionUID = -37173622378350600L;
	public static final String CONTENT_ID = "content";
	private Component innerPanel = null;

	public PanelContainer(String id) {
		super(id);
		add(innerPanel = new Label(CONTENT_ID, new Model<String>("")));
		setOutputMarkupId(true);
	}

	public void setInnerPanel(Component p) {
		checkNotNull(p);
		remove(CONTENT_ID);
		add(p);
		innerPanel = p;
	}

	public Component getInnerPanel() {
		return innerPanel;
	}
}
