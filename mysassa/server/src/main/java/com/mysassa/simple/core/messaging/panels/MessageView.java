package com.mysassa.simple.core.messaging.panels;

import com.mysassa.simple.core.messaging.model.Message;
import com.mysassa.simple.core.messaging.services.MessagingService;
import com.mysassa.simple.core.security.services.session.SecurityContext;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

/**
 * Created by Adam on 3/23/2015.
 */
public class MessageView extends Panel {
	private final Label expander;
	private final Label body;
	private boolean opened = true;

	public boolean isOpened() {
		return opened;
	}

	public void setOpened(boolean opened) {
		this.opened = opened;
	}

	@Override
	protected void onConfigure() {
		super.onConfigure();
		body.setVisible(opened);
	}

	public MessageView(String message, CompoundPropertyModel<Message> messageCompoundPropertyModel) {
		super(message, messageCompoundPropertyModel);
		setOutputMarkupId(true);

		Message m = messageCompoundPropertyModel.getObject();
		add(new AttributeModifier("class", new PropertyModel<String>(this, "cssClass")));
		add(new Label("senderContactInfo", m.getSender() != null ? m.getSender().getIdentifier() : m.getSenderContactInfo().getName()));
		add(new Label("timeSent", new Model(m.getTimeSent())));
		add(new Label("destination", m.getRecipient() != null ? "To: " + m.getRecipient().getIdentifier() : ""));
		add(body = new Label("body", new Model(m.getBody())));
		add(expander = new Label("Expander", new PropertyModel(this, "expander")));
		add(new AjaxEventBehavior("click") {
			@Override
			protected void onEvent(AjaxRequestTarget target) {
				MessagingService.get().markAsRead(m, SecurityContext.get().getUser());
				target.add(MessageView.this);
			}
		});
		expander.add(new AjaxEventBehavior("click") {
			@Override
			protected void onEvent(AjaxRequestTarget target) {
				opened = !opened;
				target.add(MessageView.this);

			}
		});
	}

	public String getCssClass() {
		if (((Message) getDefaultModelObject()).hasBeenRead(SecurityContext.get().getUser())) {
			return "MessageRowRead";
		} else {
			return "MessageRowUnread";
		}

	}

	public String getExpander() {
		return opened ? "-" : "+";
	}
}
