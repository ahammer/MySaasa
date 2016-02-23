package com.mysaasa.core.users.panels;

import com.mysaasa.core.users.model.User;
import com.mysaasa.core.users.service.UserDisabledException;
import com.mysaasa.core.users.service.UserService;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;

/**
 * Created by adam on 15-02-11.
 */
public abstract class VerifyAdmin extends Panel {
	public VerifyAdmin(String id) {
		super(id);
		add(new MyForm());
		setOutputMarkupId(true);
	}

	class MyForm extends Form {
		private final PasswordTextField fieldPassword;
		private final TextField fieldUsername;

		String username = "admin";
		String password;

		public MyForm() {
			super("updatePasswordForm");
			setOutputMarkupId(true);

			add(fieldPassword = (PasswordTextField) new PasswordTextField("password", new PropertyModel(this, "password")));
			add(fieldUsername = new TextField("username", new PropertyModel(this, "username")));

			add(new FeedbackPanel("feedback"));

			add(new AjaxSubmitLink("submit") {
				@Override
				protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
					super.onSubmit(target, form);
					User u = (User) UserService.get().getUser(username);
					if (u == null) {
						MyForm.this.error("Can not authenticate");
						target.add(MyForm.this);
						return;
					}
					User found = null;
					try {
						found = UserService.get().findUser(u.getIdentifier(), password);
					} catch (UserDisabledException e) {
						MyForm.this.error("User is disabled");
						target.add(MyForm.this);
						return;
					}
					if (found != null && found.id == u.id && u.accessLevel == User.AccessLevel.ROOT) {
						grantAccessTemporarily(target);

					} else {
						MyForm.this.error("Can not authenticate");
						target.add(MyForm.this);
					}

				}

			});

		}
	}

	protected abstract void grantAccessTemporarily(AjaxRequestTarget target);

}
