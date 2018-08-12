package com.mysaasa.pages;

import com.mysaasa.DefaultPreferences;
import com.mysaasa.MySaasa;
import com.mysaasa.core.help.panels.HelpPanel;
import com.mysaasa.core.hosting.service.HostingService;
import com.mysaasa.core.security.services.SecurityService;
import com.mysaasa.core.security.services.SessionService;
import com.mysaasa.core.security.services.session.SecurityContext;
import com.mysaasa.core.website.model.Website;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;

import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.protocol.https.RequireHttps;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.flow.RedirectToUrlException;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.List;

import static com.mysaasa.MySaasa.inject;

@RequireHttps
public class Splash extends WebPage {
	private final SigninForm signInForm;
	private static final long serialVersionUID = 1871036094132003002L;
	private final SelectSiteForm siteSelectForm;

	@Inject
	HostingService hostingService;

	public Splash() {
		inject(this);
		add(signInForm = new SigninForm());
		add(siteSelectForm = new SelectSiteForm());
		add(new HelpPanel("help", HelpPanel.Sections.SplashSignin));

		if (SessionService.get().getSecurityContext(getSession()) != null) {
			setResponsePage(Admin.class);
			return;
		}

		add(new Label("node", "N/A").setVisible(false));

		// If a nonce is provided, use it to log in
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
		@Inject
		HostingService hostingService;

		private final DropDownChoice sites;
		private final Data data = new Data();

		public static class Data implements Serializable {
			Website selected = null;
		}

		public SelectSiteForm() {
			super("ChooseSite");
			inject(this);
			String host = RequestCycle.get().getRequest().getClientUrl().getHost();
			List<Website> websites = hostingService.getWebsites();
			Website currentWebsite = hostingService.findWebsite(host);

			if (currentWebsite != null) {
				data.selected = currentWebsite;
			} else {
				throw new RedirectToUrlException("http://" + websites.get(0).production + ":" + DefaultPreferences.getPort());
			}

			sites = new DropDownChoice("sites", new PropertyModel(data, "selected"), websites, new WebsiteChoiceRenderer());

			sites.add(new AjaxFormComponentUpdatingBehavior("onchange") {
				@Override
				protected void onUpdate(AjaxRequestTarget target) {
					throw new RedirectToUrlException("http://" + data.selected.production + ":" + DefaultPreferences.getPort());
				}
			});

			add(sites);

		}
	}

}
