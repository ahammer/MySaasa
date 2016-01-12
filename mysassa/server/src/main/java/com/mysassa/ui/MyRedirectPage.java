package com.mysassa.ui;

import org.apache.wicket.Page;
import org.apache.wicket.markup.html.pages.RedirectPage;

/**
 * No default constructor, it's a bug in redirectpage.
 * Created by administrator on 2014-07-10.
 */
public class MyRedirectPage extends RedirectPage {

	public MyRedirectPage() {
		super("");
	}

	public MyRedirectPage(CharSequence url) {
		super(url);
	}

	public MyRedirectPage(CharSequence url, int waitBeforeRedirectInSeconds) {
		super(url, waitBeforeRedirectInSeconds);
	}

	public MyRedirectPage(Page page) {
		super(page);
	}

	public MyRedirectPage(Page page, int waitBeforeRedirectInSeconds) {
		super(page, waitBeforeRedirectInSeconds);
	}

}
