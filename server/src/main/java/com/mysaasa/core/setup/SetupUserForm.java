package com.mysaasa.core.setup;

import com.mysaasa.DefaultPreferences;
import com.mysaasa.core.organization.model.Organization;
import com.mysaasa.core.organization.services.OrganizationService;
import com.mysaasa.core.users.model.User;
import com.mysaasa.core.users.service.UserService;
import com.mysaasa.messages.SetupMessage;
import com.mysaasa.Simple;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;

import java.io.Serializable;

/**
* Created by Adam on 4/11/14.
*/
public class SetupUserForm extends Form {

	private final WebMarkupContainer ready;

	public static class SetupUserFormData implements Serializable {
		private String username;
		private String password;
		private String organization;
		private String baseDomain;
		private String backup = "";

		public String getBackup() {
			return backup;
		}

		public void setBackup(String backup) {
			this.backup = backup;
		}

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		public String getOrganization() {
			return organization;
		}

		public void setOrganization(String organization) {
			this.organization = organization;
		}
	}

	SetupUserFormData data = new SetupUserFormData();

	private final FormComponent<String> baseDomain = new TextField("baseDomain").setRequired(true);
	private final FormComponent<String> email = new TextField("username").setRequired(false);
	private final FormComponent<String> pass = new PasswordTextField("password").setRequired(false);
	private final FormComponent<String> org = new TextField<String>("organization").setRequired(false);
	private final WebMarkupContainer usernameLabel;
	private final WebMarkupContainer passwordLabel;
	private final WebMarkupContainer orgLabel;
	private final FeedbackPanel feedback = new FeedbackPanel("feedback");

	AjaxSubmitLink button = new AjaxSubmitLink("submit", this) {
		protected void onError(final org.apache.wicket.ajax.AjaxRequestTarget target, Form<?> form) {
			send(getPage(), Broadcast.BREADTH, new SetupMessage() {
				@Override
				public AjaxRequestTarget getAjaxRequestTarget() {

					return target;
				}
			});

		}

		protected void onSubmit(final org.apache.wicket.ajax.AjaxRequestTarget target, Form<?> form) {

			Organization o = new Organization();
			o.setName(data.getOrganization());
			o = OrganizationService.get().saveOrganization(o);
			User u = new User(data.getUsername(), data.getPassword(), User.AccessLevel.ROOT);
			u.setOrganization(o);
			UserService.get().saveUser(u);
			DefaultPreferences.getProperties().setProperty("baseDomain", baseDomain.getValue());
			//                Simple.getInstance().getProperties().setProperty(Simple.getInstance().PREF_USER_INITIALIZED, "true");
			Simple.getInstance().saveProperties();
			info("Creating user");
			send(getPage(), Broadcast.BREADTH, new SetupMessage() {
				@Override
				public AjaxRequestTarget getAjaxRequestTarget() {
					return target;
				}
			});
		}
	};

	@Override
	public void onConfigure() {
		super.onConfigure();
		ready.setVisible(false);
		/*
		if (Boolean.valueOf(Simple.getInstance().getProperties().getProperty(Simple.getInstance().PREF_USER_INITIALIZED, "false"))) {
		    ready.setVisible(true);
		    email.setVisible(false);
		    pass.setVisible(false);
		    org.setVisible(false);
		    button.setVisible(false);
		    usernameLabel.setVisible(false);
		    passwordLabel.setVisible(false);
		    orgLabel.setVisible(false);
		    info("Already Initialized");
		}
		*/
	}

	public SetupUserForm(String id) {
		super(id);
		setModel(new CompoundPropertyModel(data));
		add(ready = new WebMarkupContainer("ready"));

		add(baseDomain);
		add(email);
		add(pass);
		add(org);
		add(usernameLabel = new WebMarkupContainer("usernameLabel"));
		add(passwordLabel = new WebMarkupContainer("passwordLabel"));
		add(orgLabel = new WebMarkupContainer("orgLabel"));

		add(feedback);
		add(button);
		feedback.setOutputMarkupId(true);
	}
}
