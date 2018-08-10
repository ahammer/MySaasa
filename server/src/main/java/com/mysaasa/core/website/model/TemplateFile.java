package com.mysaasa.core.website.model;

import com.mysaasa.core.hosting.service.HostingService;

import java.io.File;
import java.io.IOException;

import static com.mysaasa.MySaasa.getService;

/**
 *
 * Represents a File used by the system.
 * Supports getting a debug url
 */
public class TemplateFile extends File {
	private static final long serialVersionUID = 1l;
	private final Website website;

	public TemplateFile(String path, Website w) {
		super(path);
		if (w == null)
			throw new NullPointerException("Can't have a null website");
		if (w == null) {
			website = getService(HostingService.class).findWebsite(this);
		} else {
			website = w;
		}

	}

	public TemplateFile(String path) {
		super(path);
		website = getService(HostingService.class).findWebsite(this);
	}

	public TemplateFile(File object) {
		super(object.getAbsolutePath());
		website = getService(HostingService.class).findWebsite(this);
	}

	public Website getWebsite() {
		return website;
	}

	public String getDebugUrl() {
		String result = website.getUrl(this);
		return result;
	}

	public void invokeCompass() {
		try {
			String path;
			if (isProduction()) {
				path = getWebsite().calculateProductionRoot().getAbsolutePath();
			} else {
				path = getWebsite().calculateStagingRoot().getAbsolutePath();
			}
			Process p = Runtime.getRuntime().exec("compass compile " + path);

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private boolean isProduction() {
		return getAbsolutePath().contains(getWebsite().getProduction());
	}
}
