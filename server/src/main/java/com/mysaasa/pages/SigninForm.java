package com.mysaasa.pages;

import com.mysaasa.core.security.services.SessionService;
import com.mysaasa.core.users.model.User;
import com.mysaasa.core.users.service.UserDisabledException;
import com.mysaasa.core.users.service.UserService;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;

import java.io.Serializable;

public class SigninForm<T> extends Form<T> {
	private final TextField<String> identifier;
	private final PasswordTextField password;
	private final Component label;

	public SigninForm() {
		super("Signin", new CompoundPropertyModel(data = new SignInData()));
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
				// Todo Move sign in to security Service
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

	public TextField<String> getIdentifier() {
		return identifier;
	}

	public PasswordTextField getPassword() {
		return password;
	}

	public static class SignInData implements Serializable {
		private static final long serialVersionUID = -7820420965519910560L;
		String identifier;
		String password;

		public SignInData() {}

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
	private static SignInData data;

	public void setMessage(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	private String message = "";

}
