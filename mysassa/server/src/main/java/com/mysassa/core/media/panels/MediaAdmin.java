package com.mysassa.core.media.panels;

import org.apache.wicket.markup.html.panel.Panel;

public class MediaAdmin extends Panel {
	private static final long serialVersionUID = -8265087544848152254L;

	public MediaAdmin(String id) {
		super(id);
		add(new MediaDataTable("websiteList", new MediaDataProvider(), 20));
	}

}
