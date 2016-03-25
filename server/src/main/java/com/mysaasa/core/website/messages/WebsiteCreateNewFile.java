package com.mysaasa.core.website.messages;

import com.mysaasa.core.website.model.Website;
import com.mysaasa.messages.AjaxMessage;

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
