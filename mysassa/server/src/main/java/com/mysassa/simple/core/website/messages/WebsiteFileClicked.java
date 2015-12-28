package com.mysassa.simple.core.website.messages;

import java.io.File;

import com.mysassa.simple.messages.ACTIONS;
import com.mysassa.simple.messages.AjaxIntent;
import com.mysassa.simple.core.website.model.Website;
import org.apache.wicket.ajax.AjaxRequestTarget;

public class WebsiteFileClicked extends AjaxIntent {
	private final Website website;
	private final File file;

	public WebsiteFileClicked(AjaxRequestTarget target, Website website, File file) {
		super(target);
		setAction(ACTIONS.ACTION_WEBSITE_FILE_CLICKED);
		this.website = website;
		this.file = file;
	}

	public File getFile() {
		return file;
	}

	public Website getWebsite() {
		return website;
	}

}
