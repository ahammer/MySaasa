package com.mysassa.simple.core.users.panels.editor;

import com.mysassa.simple.SimpleImpl;
import com.mysassa.simple.core.users.panels.VerifyAdmin;
import com.mysassa.simple.core.users.service.UserService;
import com.mysassa.simple.messages.MessageHelpers;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.event.Broadcast;

import com.mysassa.simple.core.users.data.UserFormData;
import com.mysassa.simple.core.users.messages.UserDataChanged.UserDeleted;
import com.mysassa.simple.core.users.model.User;
import org.apache.wicket.model.Model;

final class DeleteLink extends AjaxLink {
	private final UserEditor userEditor;
	private static final long serialVersionUID = 1L;

	DeleteLink(UserEditor userEditor) {
		super("deleteLink");
		this.userEditor = userEditor;
	}

	@Override
	public void onClick(final AjaxRequestTarget target) {

		try {
			userEditor.modal.setTitle("Are you sure you want to Delete, Carts and Blogposts will be detached?");
			userEditor.modal.setContent(new VerifyAdmin(userEditor.modal.getContentId()) {
				@Override
				protected void grantAccessTemporarily(AjaxRequestTarget target) {
					final Object modObj = userEditor.getDefaultModelObject();
					if (modObj instanceof UserFormData) {
						final User u = ((UserFormData) modObj).getUser();
						UserService service = SimpleImpl.get().getInjector().getProvider(UserService.class).get();
						service.deleteUser(u);

						send(getPage(), Broadcast.BREADTH, new UserDeleted(u) {
							@Override
							public AjaxRequestTarget getAjaxRequestTarget() {
								return target;
							}
						});
						MessageHelpers.editEventMessage(target, new Model(new User()));
						userEditor.modal.close(target);
					}
				}

			});
			userEditor.modal.show(target);

		} catch (IllegalStateException e) {
			e.printStackTrace();
			error("Can not delete this user");
			target.add(userEditor);
		}
		//-=---

	}
}
