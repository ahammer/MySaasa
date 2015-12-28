package com.mysassa.simple.core.website.messages;

import com.mysassa.simple.messages.AjaxMessage;
import com.mysassa.simple.core.website.model.Website;

public abstract class WebsiteContentUpdated extends AjaxMessage {
	private final Website website;

	public WebsiteContentUpdated(Website website) {
		super();
		this.website = website;
	}

}
