package com.mysassa.ui;

import com.mysassa.core.messaging.MessagingModule;
import com.mysassa.core.security.services.session.SecurityContext;
import com.mysassa.core.users.messages.MessageCreatedPushMessage;
import com.mysassa.core.users.model.User;
import com.mysassa.messages.DataUpdateEvent;
import com.mysassa.messages.MessageHelpers;
import com.mysassa.messages.WebsocketEvent;
import com.mysassa.pages.Splash;
import com.mysassa.core.messaging.services.MessagingService;
import com.mysassa.core.security.services.SessionService;
import com.mysassa.core.messaging.model.Message;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

public class ActiveUserInfo extends Panel {
	private static final long serialVersionUID = -7465076847094036441L;
	private final AjaxLink signOutLink;

	public String getMessageCount() {
		long unread = MessagingService.get().getUnreadMessageCount();
		if (unread == 0)
			return "No new messages";
		return unread + " Unread";
	}

	public ActiveUserInfo(final IModel<User> model) {
		super("userInfo", model);
		setOutputMarkupId(true);
		add(new Label("identifier").add(new UserClickEvent()));
		add(new Label("messageCount", new PropertyModel(ActiveUserInfo.this, "messageCount")).add(new MessageClickEvent()));
		add(signOutLink = new AjaxLink("signOut") {
			@Override
			public void onClick(AjaxRequestTarget target) {
				SessionService.get().unregisterSession(getSession());
				target.getPage().setResponsePage(Splash.class);
			}
		});
	}

	@Override
	public void onEvent(IEvent<?> event) {
		super.onEvent(event);
		if (event.getPayload() instanceof DataUpdateEvent) {
			DataUpdateEvent evt = (DataUpdateEvent) event.getPayload();
			if (evt.obj instanceof User) {
				User u = (User) evt.obj;
				if (u.id == ((User) getDefaultModelObject()).id) {
					ActiveUserInfo.this.setDefaultModelObject(u);
					evt.getAjaxRequestTarget().add(ActiveUserInfo.this);
				}
			} else if (evt.obj instanceof Message) {
				evt.getAjaxRequestTarget().add(ActiveUserInfo.this);
			}
		} else if (event.getPayload() instanceof WebsocketEvent) {
			WebsocketEvent evt = (WebsocketEvent) event.getPayload();
			if (evt.message instanceof MessageCreatedPushMessage)
				evt.handler.add(ActiveUserInfo.this);
		}
	}

	private class UserClickEvent extends AjaxEventBehavior {
		public UserClickEvent() {
			super("onclick");
		}

		@Override
		protected void onEvent(final AjaxRequestTarget target) {
			MessageHelpers.editEventMessage(target, new Model(SecurityContext.get().getUser()));
		}
	}

	private class MessageClickEvent extends AjaxEventBehavior {
		public MessageClickEvent() {
			super("onclick");
		}

		@Override
		protected void onEvent(final AjaxRequestTarget target) {
			MessageHelpers.gotoModule(target, MessagingModule.class);
		}
	}
}
