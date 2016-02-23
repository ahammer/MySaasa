package com.mysaasa.core.hosting.panels;

import org.apache.wicket.markup.html.panel.Panel;

public class HostingSidebar extends Panel {
	private static final long serialVersionUID = -8265087544848152254L;

	public HostingSidebar(String id) {
		super(id);
		WebsitesDataProvider provider;
		WebsitesDataTable table;
		add(table = new WebsitesDataTable("websiteList", provider = new WebsitesDataProvider(), 20));
	}

}
