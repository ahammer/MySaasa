package com.mysassa.core.website.messages;

import com.mysassa.messages.AjaxMessage;
import com.mysassa.core.website.model.Website;

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
