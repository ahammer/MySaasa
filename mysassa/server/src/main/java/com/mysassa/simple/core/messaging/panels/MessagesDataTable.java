package com.mysassa.simple.core.messaging.panels;

import com.mysassa.simple.core.users.messages.MessageCreatedPushMessage;
import com.mysassa.simple.core.messaging.model.Message;
import com.mysassa.simple.core.users.model.User;
import com.mysassa.simple.core.users.service.UserService;
import com.mysassa.simple.messages.DataUpdateEvent;
import com.mysassa.simple.messages.MessageHelpers;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.ISortableDataProvider;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.protocol.ws.api.WebSocketBehavior;
import org.apache.wicket.protocol.ws.api.WebSocketRequestHandler;
import org.apache.wicket.protocol.ws.api.message.ClosedMessage;
import org.apache.wicket.protocol.ws.api.message.ConnectedMessage;
import org.apache.wicket.protocol.ws.api.message.IWebSocketPushMessage;

import java.util.ArrayList;

public class MessagesDataTable extends AjaxFallbackDefaultDataTable {
	/**
	 *
	 */
	private static final long serialVersionUID = -7235830191682657603L;
	private static final ArrayList<IColumn> columns = new ArrayList<IColumn>();

	static {
		columns.add(new AbstractColumn(new Model<String>("Messages")) {
			@Override
			public void populateItem(Item cellItem, String componentId, IModel rowModel) {
				cellItem.add(new MessageRow(componentId, rowModel));
			}
		});
	}

	private final User user;

	public MessagesDataTable(String id, User u, ISortableDataProvider dataProvider) {
		super(id, columns, dataProvider, 20);
		this.user = u;
		setOutputMarkupId(true);

		add(new WebSocketBehavior() {

			@Override
			protected void onPush(WebSocketRequestHandler handler, IWebSocketPushMessage message) {
				super.onPush(handler, message);
				if (message instanceof MessageCreatedPushMessage) {
					handler.add(MessagesDataTable.this);
				}
			}

			@Override
			protected void onConnect(ConnectedMessage message) {
				super.onConnect(message);
				UserService.get().RegisterUserWebsocket(user, message.getKey(), message.getSessionId());
				message.getKey();
			}

			@Override
			protected void onClose(ClosedMessage message) {
				super.onClose(message);
				UserService.get().UnregisterUserWebsocket(user, message.getKey(), message.getSessionId());
			}

		});
	}

	// override this method of the DataTable class

	@SuppressWarnings("unchecked")
	@Override
	protected Item newRowItem(final String id, final int index, final IModel model) {

		final Item rowItem = super.newRowItem(id, index, model);
		rowItem.add(new AjaxEventBehavior("onclick") {

			private static final long serialVersionUID = 6720512493017210281L;

			@Override
			protected void onEvent(AjaxRequestTarget target) {
				if (rowItem.getModelObject() instanceof Message) {
					final Message u = (Message) rowItem.getModelObject();
					onRowClick(target, u);
				}

			}

		});
		return rowItem;

	}

	// Messages Responded to
	// --OrganizationDataChanged
	@Override
	public void onEvent(IEvent event) {
		final Object payload = event.getPayload();
		if (payload instanceof DataUpdateEvent) {
			final DataUpdateEvent msg = (DataUpdateEvent) (payload);
			if (msg.obj instanceof Message) {
				msg.getAjaxRequestTarget().add(this);
			}
		}
	}

	public void onRowClick(AjaxRequestTarget target, Message u) {
		MessageHelpers.editEventMessage(target, new Model(u));
	}

}
