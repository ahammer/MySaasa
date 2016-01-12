package com.mysassa.core.website.messages;

import com.mysassa.core.website.model.Website;
import com.mysassa.messages.AjaxMessage;

public abstract class WebsiteContentUpdated extends AjaxMessage {
	private final Website website;

	public WebsiteContentUpdated(Website website) {
		super();
		this.website = website;
	}

}
