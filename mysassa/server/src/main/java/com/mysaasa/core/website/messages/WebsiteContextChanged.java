package com.mysaasa.core.website.messages;

import com.mysaasa.messages.AjaxMessage;
import com.mysaasa.core.website.model.Website;

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
