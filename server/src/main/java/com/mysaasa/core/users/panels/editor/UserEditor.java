package com.mysaasa.core.users.panels.editor;

import com.mysaasa.core.organization.services.OrganizationService;
import com.mysaasa.core.users.model.User;
import com.mysaasa.core.users.panels.ChangePassword;
import com.mysaasa.core.users.service.UserService;
import com.mysaasa.messages.MessageHelpers;
import com.mysaasa.core.organization.model.Organization;
import com.mysaasa.core.security.services.session.SecurityContext;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.AjaxEditableLabel;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.feedback.ComponentFeedbackMessageFilter;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import com.mysaasa.core.users.data.UserFormData;
import com.mysaasa.core.users.panels.ContactInfoEditor;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.validation.*;

import java.util.Arrays;
import java.util.List;

public class UserEditor extends Panel {
	private static final long serialVersionUID = 1L;

	// Feedback and Modals
	final ModalWindow modal;
	final ModalWindow passwordModal;
	final FeedbackPanel feedbackIdentifier;
	final FeedbackPanel feedback;

	// Header Labels
	final WebMarkupContainer labelEdit;
	final WebMarkupContainer labelNew;

	// Controls
	final DropDownChoice<Organization> dropDownOrgChoice;
	final DropDownChoice<User.AccessLevel> accessLevel;
	final AjaxEditableLabel editableIdentifier;
	final DeleteLink ajaxLinkDelete;

	public UserEditor(String id, final IModel<UserFormData> model) {
		super(id, model);
		setOutputMarkupId(true);
		add(feedback = new FeedbackPanel("feedback"));
		add(passwordModal = new ModalWindow("passwordModal"));
		add(new ChangePasswordLink(model));
		add(new ContactInfoEditor("contactInfo", new CompoundPropertyModel(model.getObject().getUser().getContactInfo())) {
			@Override
			public void updated(AjaxRequestTarget target, IModel model) {
				persistModel(target);
			}
		});

		add(editableIdentifier = new AjaxEditableLabel("user.identifier") {
			@Override
			protected void onSubmit(AjaxRequestTarget target) {
				super.onSubmit(target);
				persistModel(target);

			}

			protected void onError(AjaxRequestTarget target) {
				super.onSubmit(target);
				target.add(feedback);
				target.add(feedbackIdentifier);
			}
		}.add(new UserIdentifierValidator(model)).setLabel(new Model("Identifier")).setRequired(true));
		add(accessLevel = new DropDownChoice<User.AccessLevel>("user.accessLevel", Arrays.asList(User.AccessLevel.values())));
		add(feedbackIdentifier = (FeedbackPanel) new FeedbackPanel("feedbackIdentifier", new ComponentFeedbackMessageFilter(editableIdentifier)).setOutputMarkupId(true));
		add(labelEdit = new WebMarkupContainer("EditLabel"));
		add(labelNew = new WebMarkupContainer("NewLabel"));
		add(modal = new ModalWindow("modal"));
		add(ajaxLinkDelete = new DeleteLink(UserEditor.this));
		add(dropDownOrgChoice = new DropDownChoice<Organization>("user.organization", new AllOrganizationsModel()));

		dropDownOrgChoice.setRequired(true);
		ajaxLinkDelete.setOutputMarkupId(true);
		feedback.setOutputMarkupId(true);

		accessLevel.add(new UserEditorFieldUpdatedBehavior());

	}

	@Override
	protected void onConfigure() {
		super.onConfigure();
		User user = ((UserFormData) getDefaultModelObject()).getUser();
		labelEdit.setVisible(user.getId() != 0);
		labelNew.setVisible(user.getId() == 0);
		ajaxLinkDelete.setVisible(user.getId() != 0 && user.getId() != SecurityContext.get().getUser().getId());
		User.AccessLevel level = SecurityContext.get().getUser().accessLevel;
		accessLevel.setEnabled(level == User.AccessLevel.ROOT && (SecurityContext.get().getUser().getId() != ((UserFormData) getDefaultModelObject()).getUser().getId()));
	}

	private void persistModel(AjaxRequestTarget target) {
		UserFormData formData = (UserFormData) this.getDefaultModelObject();
		User user = formData.getUser();
		// If ready to create (id == 0 and we have a ID and Email
		if (!userReadyToCreate()) {
			error("Email and ID Required");
		} else {
			if (user.id == 0) {
				info("Created user");
			} else {
				info("Saved user");
			}
			this.setDefaultModelObject(new UserFormData(user = UserService.get().saveUser(user)));
			MessageHelpers.notifyUpdate(target, user);
			target.add(this);
		}
		target.add(feedbackIdentifier);
		target.add(feedback);
	}

	private boolean userReadyToCreate() {
		UserFormData formData = (UserFormData) this.getDefaultModelObject();
		User user = formData.getUser();
		boolean hasIdentifier = user.getIdentifier() != null && user.getIdentifier().length() > 3;
		boolean hasEmail = user.getContactInfo().getEmail() != null && user.getContactInfo().getEmail().length() > 3;
		boolean result = hasIdentifier && hasEmail;
		return result;

	}

	private static class UserIdentifierValidator implements IValidator {
		private final IModel<UserFormData> model;

		public UserIdentifierValidator(IModel<UserFormData> model) {
			this.model = model;
		}

		@Override
		public void validate(IValidatable validatable) {
			Object value = validatable.getValue();
			if (value instanceof String) {
				String identifier = (String) value;
				if (identifier.trim().length() < 3) {
					validatable.error(new ValidationError("Too short"));
				} else {
					User u = UserService.get().getUser(identifier);
					if (u != null && u.id != model.getObject().getUser().id) {
						validatable.error(new ValidationError("ID Taken"));
					}
				}
			}
		}
	}

	private static class AllOrganizationsModel extends LoadableDetachableModel<List<Organization>> {
		@Override
		protected List<Organization> load() {
			return OrganizationService.get().getAllOrganizations();
		}
	}

	private class ChangePasswordLink extends AjaxLink {
		private final IModel<UserFormData> model;

		public ChangePasswordLink(IModel<UserFormData> model) {
			super("changePassword");
			this.model = model;
		}

		@Override
		public void onClick(AjaxRequestTarget target) {
			passwordModal.setTitle("Update Password");
			passwordModal.setContent(new ChangePassword(passwordModal.getContentId(), new CompoundPropertyModel<User>(model.getObject().getUser())) {
				@Override
				protected void success(AjaxRequestTarget target1) {
					passwordModal.close(target1);
					info("Password Updated");
					target1.add(UserEditor.this);
				}
			});
			passwordModal.setInitialHeight(214);
			passwordModal.setInitialWidth(720);
			passwordModal.show(target);
			passwordModal.setResizable(false);
		}
	}

	/**
	 * A Behavior for when fields are changed
	 */
	private class UserEditorFieldUpdatedBehavior extends AjaxFormComponentUpdatingBehavior {
		public UserEditorFieldUpdatedBehavior() {
			super("change");
		}

		@Override
		protected void onUpdate(AjaxRequestTarget target) {
			persistModel(target);
		}

		@Override
		protected void onError(AjaxRequestTarget target, RuntimeException e) {
			super.onError(target, e);
			persistModel(target);
		}
	}
}
