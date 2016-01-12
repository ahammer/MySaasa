package com.mysassa.core.hosting.messages;

import com.mysassa.core.website.model.Website;
import com.mysassa.messages.ACTIONS;
import com.mysassa.messages.AjaxIntent;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.IEventSource;

import static com.google.common.base.Preconditions.checkNotNull;

public class WebsiteDataChanged extends AjaxIntent {
	private final Website website;

	WebsiteDataChanged(Website w, AjaxRequestTarget target) {
		super(target);
		website = w;
	}

	WebsiteDataChanged(Website w, IEventSource target) {
		super(target);
		website = w;
	}

	public static class WebsiteCreated extends WebsiteDataChanged {
		public WebsiteCreated(Website w, AjaxRequestTarget target) {
			super(w, target);
			checkNotNull(w);
			checkNotNull(w.calculateProductionRoot());
			setAction(ACTIONS.ACTION_WEBSITE_CREATED);
		}
	}

	public static class WebsiteDeleted extends WebsiteDataChanged {
		public WebsiteDeleted(Website w, AjaxRequestTarget target) {
			super(w, target);
			checkNotNull(w);
			setAction(ACTIONS.ACTION_WEBSITE_DELETED);
		}
	}

	public static class WebsiteSelected extends WebsiteDataChanged {
		public WebsiteSelected(Website w, IEventSource target) {
			super(w, target);
			checkNotNull(w);
			setAction(ACTIONS.ACTION_WEBSITE_SELECTED);
		}
	}
}
