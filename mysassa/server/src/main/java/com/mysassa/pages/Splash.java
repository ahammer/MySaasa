package com.mysassa.pages;

import java.io.Serializable;

import com.mysassa.Simple;
import com.mysassa.core.help.panels.HelpPanel;
import com.mysassa.core.security.services.SecurityService;
import com.mysassa.core.security.services.SessionService;
import com.mysassa.core.security.services.session.SecurityContext;
import com.mysassa.core.users.service.UserDisabledException;
import com.mysassa.core.users.service.UserService;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;

import com.mysassa.core.users.model.User;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.protocol.https.RequireHttps;

@RequireHttps
public class Splash extends WebPage {
	private final SigninForm signInForm;
	private static final long serialVersionUID = 1871036094132003002L;

	public Splash() {
		SessionService.get().unregisterSession(getSession());

		add(signInForm = new SigninForm());
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

	public static class SigninForm<T> extends Form<T> {
		private final TextField<String> identifier;
		private final PasswordTextField password;
		private final Component label;

		public TextField<String> getIdentifier() {
			return identifier;
		}

		public PasswordTextField getPassword() {
			return password;
		}

		public static class Data implements Serializable {
			private static final long serialVersionUID = -7820420965519910560L;
			String identifier;
			String password;

			public Data() {}

			public String getIdentifier() {
				return identifier;
			}

			public String getPassword() {
				return password;
			}
		}

		/**
		 * 
		 */
		private static final long serialVersionUID = -3785722862517119670L;
		private static Data data;

		public void setMessage(String message) {
			this.message = message;
		}

		public String getMessage() {
			return message;
		}

		private String message = "";

		public SigninForm() {
			super("Signin", new CompoundPropertyModel(data = new Data()));
			setOutputMarkupId(true);
			add(identifier = new TextField<String>("identifier"));
			add(password = (PasswordTextField) new PasswordTextField("password").setRequired(false));
			add(label = new Label("message", new PropertyModel<String>(this, "message")).setOutputMarkupId(true));
			add(new AjaxSubmitLink("RecoverPassword") {
				@Override
				protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
					super.onSubmit(target, form);
					String identifier = data.identifier;
					if (UserService.get().provideEmailAccess(identifier)) {
						setMessage("Password sent to " + identifier + "'s Email Address");
					} else {
						setMessage("Error");
					}
					target.add(label);

				}
			});

			add(new AjaxSubmitLink("Signin") {
				@Override
				protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
					super.onSubmit(target, form);
					User u = null;
					try {
						u = UserService.get().findUser(data.getIdentifier(), data.getPassword());
					} catch (UserDisabledException e) {
						setMessage("User Disabled");
						target.add(label);
						return;
					}
					//Todo Move sign in to security Service
					if (u == null) {
						setMessage("Invalid Username/Password");
					} else {
						SessionService.get().registerUser(getSession(), u);
						setResponsePage(Admin.class);
					}
					target.add(label);
				}
			});
		}
	}

}
