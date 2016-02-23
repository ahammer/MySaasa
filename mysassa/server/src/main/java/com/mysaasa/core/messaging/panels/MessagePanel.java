package com.mysaasa.core.messaging.panels;

import com.mysaasa.core.users.panels.VerifyAdmin;
import com.mysaasa.messages.MessageHelpers;
import com.mysaasa.messages.WebsocketEvent;
import com.mysaasa.core.messaging.services.MessagingService;
import com.mysaasa.core.messaging.model.Message;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.PropertyModel;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

/**
 * Created by adam on 14-12-09.
 */
public class MessagePanel extends Panel {

	private static CompoundPropertyModel<Message> model;
	private final ListView list;

	@Override
	protected void onConfigure() {
		super.onConfigure();

	}

	@Override
	public void renderHead(IHeaderResponse response) {
		response.render(OnDomReadyHeaderItem.forScript("var elem=(document.getElementById('messageList'));  elem.scrollTop = elem.scrollHeight;"));
	}

	public MessagePanel(String id, final Message m) {

		super(id, model = new CompoundPropertyModel(m));
		checkNotNull(m);

		setOutputMarkupId(true);

		ModalWindow modal;
		add(modal = new ModalWindow("modal"));
		add(new Label("title", new PropertyModel(m, "title")));

		add(new Label("sender.identifier"));
		add(new Label("recipient.identifier"));

		//add(new Label("body"));
		add(list = new ListView("messageList", new PropertyModel(this, "thread")) {
			@Override
			protected void populateItem(ListItem item) {
				Message msg = ((Message) item.getModelObject());
				MessageView messageView;
				item.add(messageView = new MessageView("message", new CompoundPropertyModel<Message>(msg)));

				if (msg.id == m.id) {
					messageView.setOpened(true);
				}
				if (msg.body == null || msg.body.equals(""))
					item.setVisible(false);
			}
		});

		list.setOutputMarkupId(true);
		if (model.getObject().getId() == 0) {
			add(new Label("reply", ""));
		} else {

			add(new MessageReplyPanel("reply", model.getObject().getMessageThreadRoot() == null ? model : new CompoundPropertyModel<Message>(model.getObject().getMessageThreadRoot())) {
				@Override
				protected void replyComplete(AjaxRequestTarget target) {
					MessagePanel.this.resetThreadList();
					//target.add(MessagePanel.this);
				}
			});
		}

		add(new AjaxLink("delete") {
			@Override
			public void onClick(AjaxRequestTarget target) {
				modal.setTitle("Are you sure you want to Delete, Carts and Blogposts will be detached?");
				modal.setContent(new VerifyAdmin(modal.getContentId()) {
					@Override
					protected void grantAccessTemporarily(AjaxRequestTarget target) {
						MessagingService.get().deleteMessage(model.getObject());
						MessageHelpers.notifyUpdate(target, model.getObject());
						modal.close(target);
					}
				});
				modal.show(target);
			}
		});

		add(new AjaxLink("hideThread") {
			@Override
			public void onClick(AjaxRequestTarget target) {

			}
		});

		//Mark as read
		//model.getObject().setRead(true);
		//MessagingService.get().saveMessage(model.getObject());
	}

	@Override
	public void onEvent(IEvent<?> event) {
		super.onEvent(event);
		if (event.getPayload() instanceof WebsocketEvent) {
			WebsocketEvent evt = (WebsocketEvent) event.getPayload();
			if (evt.message instanceof MessageThreadUpdatedMessaged) {
				MessageThreadUpdatedMessaged msg = (MessageThreadUpdatedMessaged) evt.message;
				//Message m = model.getObject();
				MessagePanel.this.resetThreadList();

				evt.handler.add(MessagePanel.this);

			}
		}
	}

	List<Message> thread;

	public void resetThreadList() {
		thread = null;
	}

	public List<Message> getThread() {
		if (thread == null) {
			thread = MessagingService.get().getThread(model.getObject());
		}
		return thread;
	}

}
