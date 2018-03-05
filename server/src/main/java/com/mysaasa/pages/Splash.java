package com.mysaasa.pages;

import com.mysaasa.Simple;
import com.mysaasa.core.help.panels.HelpPanel;
import com.mysaasa.core.hosting.service.HostingService;
import com.mysaasa.core.security.services.SecurityService;
import com.mysaasa.core.security.services.SessionService;
import com.mysaasa.core.security.services.session.SecurityContext;
import com.mysaasa.core.website.model.Website;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;

import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.protocol.https.RequireHttps;

import java.io.Serializable;
import java.util.List;

@RequireHttps
public class Splash extends WebPage {
	private final SigninForm signInForm;
	private static final long serialVersionUID = 1871036094132003002L;
	private final SelectSiteForm siteSelectForm;

	public Splash() {
		SessionService.get().unregisterSession(getSession());

		add(signInForm = new SigninForm());
		add(siteSelectForm = new SelectSiteForm());
		add(new HelpPanel("help", HelpPanel.Sections.SplashSignin));


		if (SessionService.get().getSecurityContext(getSession()) != null) {
			setResponsePage(Admin.class);
			return;
		}

		add(new Label("node", Simple.getBaseDomain()));

		//If a nonce is provided, use it to log in
		if (getRequest().getQueryParameters().getParameterNames().contains("nonce")) {
			String nonce_key = getRequest().getQueryParameters().getParameterValue("nonce").toOptionalString();
			SecurityService.SigninNonce nonce = SecurityService.get().getNonce(nonce_key);
			if (nonce != null) {
				SessionService.get().registerUser(getSession(), nonce.u);
				SecurityContext.get().nonce_signin = true;
				setResponsePage(Admin.class);

			}
	}
	}


	public SigninForm getSignInForm() {
		return signInForm;
	}

	public static class SelectSiteForm<T> extends Form<T> {


		private final DropDownChoice sites;
		private final Data data = new Data();

		public static class Data implements Serializable {
			Website selected = null;
		}

		public SelectSiteForm() {
			super("ChooseSite");
			List<Website> websites = HostingService.get().getWebsites();

			sites = new DropDownChoice("sites", new PropertyModel(data, "selected"), websites, new WebsiteChoiceRenderer());
			add(sites);

		}
	}

	private static class WebsiteChoiceRenderer implements IChoiceRenderer<Website> {

		@Override
		public Object getDisplayValue(Website object) {
			return object.production;
		}

		@Override
		public String getIdValue(Website object, int index) {
			return String.valueOf(object.getId());
		}

		@Override
		public Website getObject(String id, IModel<? extends List<? extends Website>> choices) {
			for (Website obj : choices.getObject()) {
				if (String.valueOf(obj.getId()).equalsIgnoreCase(id))
					return  obj;
			}
			return null;
		}
	}

}
