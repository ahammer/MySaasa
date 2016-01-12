package com.mysassa.core.website.panels;

import com.mysassa.core.website.model.Website;
import com.mysassa.messages.MessageHelpers;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;

/**
 * Put this link on a page reference a website, and it will broadcast the message that will load the parts
 *
* Created by adam on 2014-10-13.
*/
public class LoadWebsiteAjaxLink extends AjaxLink {
	private final Website website;

	public LoadWebsiteAjaxLink(String id, Website website) {
		super(id);
		this.website = website;
	}

	@Override
	public void onClick(AjaxRequestTarget target) {
		MessageHelpers.loadWebsiteEditor(target, website);
	}
}
