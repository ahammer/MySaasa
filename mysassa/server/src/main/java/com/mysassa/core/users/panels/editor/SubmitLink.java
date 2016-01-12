package com.mysassa.core.users.panels.editor;

import com.mysassa.SimpleImpl;
import com.mysassa.core.users.model.User;
import com.mysassa.core.users.service.UserService;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.html.form.Form;

import com.mysassa.core.users.data.UserFormData;
import com.mysassa.core.users.messages.UserDataChanged.UserUpdated;

class SubmitLink extends AjaxSubmitLink {
	/**
	 * 
	 */
	private final UserEditor userEditor;
	private static final long serialVersionUID = 1L;

	SubmitLink(UserEditor userEditor) {
		super("submitLink");
		this.userEditor = userEditor;
	}

	@Override
	public void onError(AjaxRequestTarget target, Form<?> form) {
		target.add(userEditor.feedback);
	}

	@Override
	public void onSubmit(final AjaxRequestTarget target, Form<?> form) {
		final Object modObj = form.getModelObject();
		if (modObj instanceof UserFormData) {
			final User u = ((UserFormData) modObj).getUser();

			if (((UserFormData) modObj).getPassword() != null) {
				u.setPassword_md5(User.calculatePasswordHash(((UserFormData) modObj).getPassword()));
			}

			SimpleImpl.get().getInjector().getProvider(UserService.class).get().saveUser(u);
			send(getPage(), Broadcast.BREADTH, new UserUpdated(u) {
				@Override
				public AjaxRequestTarget getAjaxRequestTarget() {
					return target;
				}
			});
		}
	}
}
