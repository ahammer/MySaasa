package com.mysaasa.core.website.messages;

import com.mysaasa.core.website.model.Website;
import com.mysaasa.messages.AjaxMessage;

public abstract class WebsiteContentUpdated extends AjaxMessage {
	private final Website website;

	public WebsiteContentUpdated(Website website) {
		super();
		this.website = website;
	}

}
