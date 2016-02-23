package com.mysaasa.core.website.messages;

import java.io.File;

import com.mysaasa.core.website.model.Website;
import com.mysaasa.messages.ACTIONS;
import com.mysaasa.messages.AjaxIntent;
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
