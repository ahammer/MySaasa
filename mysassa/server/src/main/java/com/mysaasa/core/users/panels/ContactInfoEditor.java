package com.mysaasa.core.users.panels;

import com.mysaasa.core.users.model.ContactInfo;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.AjaxEditableLabel;
import org.apache.wicket.feedback.ComponentFeedbackMessageFilter;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

public abstract class ContactInfoEditor extends Panel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6389208660285577169L;
	private FeedbackPanel bitcoinAddressFeedback;
	private FeedbackPanel mobilePhoneFeedback;
	private FeedbackPanel homePhoneFeedback;
	private FeedbackPanel postalFeedback;
	private FeedbackPanel address2Feedback;
	private FeedbackPanel address1Feedback;
	private FeedbackPanel provinceFeedback;
	private FeedbackPanel cityFeedback;
	private FeedbackPanel countryFeedback;
	private FeedbackPanel emailFeedback;
	private FeedbackPanel nameFeedback;
	private ContactInfoHandler bitcoinAddress;
	private ContactInfoHandler name;
	private ContactInfoHandler email;
	private ContactInfoHandler mobilePhone;
	private ContactInfoHandler homePhone;
	private ContactInfoHandler address1;
	private ContactInfoHandler address2;
	private ContactInfoHandler postal;
	private ContactInfoHandler country;
	private ContactInfoHandler city;
	private ContactInfoHandler province;

	public ContactInfoEditor(String id, IModel<ContactInfo> model) {
		super(id, model);
		init();
	}

	public ContactInfoEditor(String id) {
		super(id);
		init();
	}

	private void init() {
		add(name = new ContactInfoHandler("name"));
		add(email = new ContactInfoHandler("email"));
		add(country = new ContactInfoHandler("country"));
		add(city = new ContactInfoHandler("city"));
		add(province = new ContactInfoHandler("province"));
		add(address1 = new ContactInfoHandler("address1"));
		add(address2 = new ContactInfoHandler("address2"));
		add(postal = new ContactInfoHandler("postal"));
		add(homePhone = new ContactInfoHandler("homePhone"));
		add(mobilePhone = new ContactInfoHandler("mobilePhone"));
		add(bitcoinAddress = new ContactInfoHandler("bitcoinAddress"));

		add(nameFeedback = (FeedbackPanel) (new FeedbackPanel("nameFeedback", new ComponentFeedbackMessageFilter(name)).setOutputMarkupId(true)));
		add(emailFeedback = (FeedbackPanel) (new FeedbackPanel("emailFeedback", new ComponentFeedbackMessageFilter(email)).setOutputMarkupId(true)));
		add(countryFeedback = (FeedbackPanel) (new FeedbackPanel("countryFeedback", new ComponentFeedbackMessageFilter(country)).setOutputMarkupId(true)));
		add(cityFeedback = (FeedbackPanel) (new FeedbackPanel("cityFeedback", new ComponentFeedbackMessageFilter(city)).setOutputMarkupId(true)));
		add(provinceFeedback = (FeedbackPanel) (new FeedbackPanel("provinceFeedback", new ComponentFeedbackMessageFilter(province)).setOutputMarkupId(true)));
		add(address1Feedback = (FeedbackPanel) (new FeedbackPanel("address1Feedback", new ComponentFeedbackMessageFilter(address1)).setOutputMarkupId(true)));
		add(address2Feedback = (FeedbackPanel) (new FeedbackPanel("address2Feedback", new ComponentFeedbackMessageFilter(address2)).setOutputMarkupId(true)));
		add(postalFeedback = (FeedbackPanel) (new FeedbackPanel("postalFeedback", new ComponentFeedbackMessageFilter(postal)).setOutputMarkupId(true)));
		add(homePhoneFeedback = (FeedbackPanel) (new FeedbackPanel("homePhoneFeedback", new ComponentFeedbackMessageFilter(homePhone)).setOutputMarkupId(true)));
		add(mobilePhoneFeedback = (FeedbackPanel) (new FeedbackPanel("mobilePhoneFeedback", new ComponentFeedbackMessageFilter(mobilePhone)).setOutputMarkupId(true)));
		add(bitcoinAddressFeedback = (FeedbackPanel) (new FeedbackPanel("bitcoinAddressFeedback", new ComponentFeedbackMessageFilter(bitcoinAddress)).setOutputMarkupId(true)));

	}

	public void disableFields() {
		for (int i = 0; i < size(); i++) {
			get(i).setEnabled(false);
		}
	}

	private class ContactInfoHandler extends AjaxEditableLabel<String> {
		public ContactInfoHandler(String name) {
			super(name);
		}

		@Override
		protected void onSubmit(AjaxRequestTarget target) {
			super.onSubmit(target);
			updated(target, ContactInfoEditor.this.getDefaultModel());
		}
	}

	public abstract void updated(AjaxRequestTarget target, IModel model);

}
