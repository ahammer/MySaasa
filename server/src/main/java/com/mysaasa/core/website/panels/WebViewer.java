package com.mysaasa.core.website.panels;

import com.mysaasa.core.blog.messages.BlogPostModifiedMessage;
import com.mysaasa.core.website.messages.WebsiteContentUpdated;
import com.mysaasa.core.website.model.TemplateFile;
import com.mysaasa.messages.ACTIONS;
import com.mysaasa.messages.AjaxIntent;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;

import org.apache.wicket.model.IModel;

import static com.google.common.base.Preconditions.checkNotNull;

public class WebViewer extends Panel {

	/**
	 *
	 */
	private static final long serialVersionUID = -1587372719514535584L;
	private Component frame;

	public WebViewer(String id, IModel<TemplateFile> model) {
		super(id, model);
		setOutputMarkupId(true);
		// add( new Label("frame", model));
		if (model.getObject() != null) {
			add(frame = new WebMarkupContainer("frame").add(new AttributeModifier("src", model.getObject().getDebugUrl())));
		} else {
			add(frame = new WebMarkupContainer("frame"));
		}
	}

	/* Handle 3 events - Media Context Changed - File has been clicked - BlogAdmin has changed
	 *
	 * In both cases we show the most relevant thing
	 *
	 * (non-Javadoc)
	 *
	 * @see org.apache.wicket.Component#onEvent(org.apache.wicket.event.IEvent) */
	@Override
	public void onEvent(IEvent event) {
		if (event.getPayload() instanceof WebsiteContentUpdated) {
			WebsiteContentUpdated wcu = (WebsiteContentUpdated) event.getPayload();
			wcu.getAjaxRequestTarget().add(this);
		} else if (event.getPayload() instanceof BlogPostModifiedMessage) {
			final BlogPostModifiedMessage msg = (BlogPostModifiedMessage) (event.getPayload());
			if (msg.getAjaxRequestTarget() != null) {
				msg.getAjaxRequestTarget().add(this);
			}
		} else if (event.getPayload() instanceof AjaxIntent) {
			AjaxIntent intent = (AjaxIntent) event.getPayload();

			if (intent.getAction().equals(ACTIONS.ACTION_USER_PREFS_UPDATED)) {
				intent.getAjaxRequestTarget().add(this);
			}

			if (intent.getAction().equals(ACTIONS.ACTION_WEBSITE_IFRAME_PAGELOAD)) {
				this.setDefaultModelObject(new TemplateFile(intent.getExtras().getString("file")));

			}

		}

	}

}
