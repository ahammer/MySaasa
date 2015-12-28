package com.mysassa.simple.core.website.messages;

import com.mysassa.simple.core.website.model.Website;
import com.mysassa.simple.messages.AjaxMessage;

public abstract class WebsiteCreateNewFile extends AjaxMessage {

	private final Website website;

	public WebsiteCreateNewFile(Website website) {
		super();
		this.website = website;
	}

	public Website getWebsite() {
		return website;
	}
}
