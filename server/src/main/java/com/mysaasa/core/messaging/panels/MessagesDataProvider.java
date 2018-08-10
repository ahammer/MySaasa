package com.mysaasa.core.messaging.panels;

import com.mysaasa.MySaasa;
import com.mysaasa.core.users.model.User;
import com.mysaasa.core.messaging.model.Message;
import com.mysaasa.core.messaging.services.MessagingService;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import java.util.ArrayList;
import java.util.Iterator;

import static com.mysaasa.MySaasa.getService;

public class MessagesDataProvider extends SortableDataProvider<Message, String> {
	private static final long serialVersionUID = 1L;
	private final User user;

	public MessagesDataProvider(User u) {
		MySaasa.inject(this);
		this.user = u;

	}

	@Override
	public Iterator<? extends Message> iterator(long first, long count) {

		new ArrayList<User>();
		final Iterator<? extends Message> result = getService(MessagingService.class).getMessages(user, first / count, count, "timeSent", "DESC").iterator();
		return result;
	}

	@Override
	public IModel<Message> model(Message object) {
		return new CompoundPropertyModel(object);
	}

	@Override
	public long size() {
		return getService(MessagingService.class).getMessageCount();
	}
}
