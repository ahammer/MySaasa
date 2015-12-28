package com.mysassa.simple.core.messaging.panels;

import com.mysassa.simple.Simple;
import com.mysassa.simple.core.messaging.model.Message;
import com.mysassa.simple.core.messaging.services.MessagingService;
import com.mysassa.simple.core.users.model.User;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Iterator;

public class MessagesDataProvider extends SortableDataProvider<Message, String> {
	private static final long serialVersionUID = 1L;
	private final User user;

	public MessagesDataProvider(User u) {
		this.user = u;
	}

	@Override
	public Iterator<? extends Message> iterator(long first, long count) {

		new ArrayList<User>();
		EntityManager em = Simple.getEm();
		final Iterator<? extends Message> result = MessagingService.get().getMessages(user, first / count, count, "timeSent", "DESC").iterator();
		em.close();
		return result;
	}

	@Override
	public IModel<Message> model(Message object) {
		return new CompoundPropertyModel(object);
	}

	@Override
	public long size() {
		return MessagingService.get().getMessageCount();
	}
}
