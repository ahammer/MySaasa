package com.mysassa.core.users.panels;

import com.mysassa.core.security.services.session.SecurityContext;
import com.mysassa.core.users.model.User;
import com.mysassa.core.users.service.UserService;
import com.mysassa.messages.MessageHelpers;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.validation.IFormValidator;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;

/**
 * Created by adam on 15-02-11.
 */
public abstract class ChangePassword extends Panel {
	public ChangePassword(String id, IModel<User> model) {
		super(id, model);
		add(new MyForm());
	}

	class MyForm extends Form {
		@Override
		protected void onConfigure() {
			super.onConfigure();
			if (SecurityContext.get().nonce_signin || SecurityContext.get().getUser().accessLevel == User.AccessLevel.ROOT) {
				fieldOldPassword.setEnabled(false);
			}
		}

		private final PasswordTextField fieldNewPassword;
		private final PasswordTextField fieldRepeatPassword;
		private final PasswordTextField fieldOldPassword;

		String oldPassword;
		String newPassword;
		String repeat;

		public String getOldPassword() {
			return oldPassword;
		}

		public void setOldPassword(String oldPassword) {
			this.oldPassword = oldPassword;
		}

		public String getNewPassword() {
			return newPassword;
		}

		public void setNewPassword(String newPassword) {
			this.newPassword = newPassword;
		}

		public String getRepeat() {
			return repeat;
		}

		public void setRepeat(String repeat) {
			this.repeat = repeat;
		}

		public MyForm() {
			super("updatePasswordForm");
			setOutputMarkupId(true);
			add(fieldOldPassword = (PasswordTextField) new PasswordTextField("oldPassword", new PropertyModel(this, "oldPassword")).add(new IValidator<String>() {
				@Override
				public void validate(IValidatable<String> validatable) {
					if (!SecurityContext.get().nonce_signin || SecurityContext.get().getUser().accessLevel == User.AccessLevel.ROOT) {
						User u = (User) ChangePassword.this.getDefaultModelObject();
						User found = UserService.get().findUser(u.getIdentifier(), validatable.getValue());
						if (found != null && found.id == u.id) {
							return;
						}
						validatable.error(new ValidationError("Password appears incorrect"));
					}
				}
			}));

			add(fieldNewPassword = new PasswordTextField("newPassword", new PropertyModel(this, "oldPassword")));
			add(fieldRepeatPassword = new PasswordTextField("repeat", new PropertyModel(this, "oldPassword")));

			add(new FeedbackPanel("feedback"));

			add(new IFormValidator() {
				@Override
				public FormComponent<?>[] getDependentFormComponents() {
					return new FormComponent<?>[0];
				}

				@Override
				public void validate(Form<?> form) {
					MyForm myForm = (MyForm) form;
					String newPass = myForm.fieldNewPassword.getConvertedInput();
					String repeat = myForm.fieldRepeatPassword.getConvertedInput();

					if (newPass == null || repeat == null) {
						error("No Blanks");
						return;
					}

					if (!repeat.equals(newPass)) {
						error("Passwords do not match");
						return;
					}
				}
			});
			add(new AjaxSubmitLink("submit") {
				@Override
				protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
					super.onSubmit(target, form);
					User u = (User) ChangePassword.this.getDefaultModelObject();
					u.setPassword(fieldNewPassword.getValue());
					MessageHelpers.notifyUpdate(target, UserService.get().saveUser(u));
					ChangePassword.this.success(target);

				}

				@Override
				protected void onError(AjaxRequestTarget target, Form<?> form) {
					super.onError(target, form);
					target.add(MyForm.this);

				}
			});

		}
	}

	protected abstract void success(AjaxRequestTarget target);

}
