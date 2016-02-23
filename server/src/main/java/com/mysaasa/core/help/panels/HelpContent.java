package com.mysaasa.core.help.panels;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

/**
 * Created by Adam on 12/14/2014.
 */
public class HelpContent extends Panel {
	public HelpContent(String id, IModel<?> model) {
		super(id, model);
	}

	public HelpContent(String id) {
		super(id);
	}

}
