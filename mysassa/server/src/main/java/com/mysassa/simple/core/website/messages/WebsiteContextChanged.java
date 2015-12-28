package com.mysassa.simple.core.website.messages;

import com.mysassa.simple.messages.AjaxMessage;
import com.mysassa.simple.core.website.model.Website;

public abstract class WebsiteContextChanged extends AjaxMessage {
	private final Website website;

	public WebsiteContextChanged(Website website) {
		super();
		this.website = website;
	}

	public Website getWebsite() {
		return website;
	}
}
