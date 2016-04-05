package com.mysaasa.core.messaging.services;

import com.google.common.collect.Lists;
import com.mysaasa.core.messaging.model.MessageReadReceipt;
import com.mysaasa.core.security.services.session.SecurityContext;
import com.mysaasa.core.users.messages.MessageCreatedPushMessage;
import com.mysaasa.core.users.model.User;
import com.mysaasa.core.users.service.UserService;
import com.mysaasa.interfaces.annotations.SimpleService;
import com.mysaasa.Simple;
import com.mysaasa.core.messaging.model.Message;
import com.mysaasa.core.messaging.panels.MessageThreadUpdatedMessaged;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.*;

import static com.google.gson.internal.$Gson$Preconditions.checkNotNull;

/**
 * Created by Adam on 3/27/2015.
 */
@SimpleService
public class MessagingService {

	public static final String SUPPORT_THREAD = "Support Thread";

	public static MessagingService get() {
		return Simple.get().getInjector().getProvider(MessagingService.class).get();
	}

	public Message saveMessage(Message msg, boolean notify) {
		long initialId = msg.getId();
		EntityManager em = Simple.getEm();
		em.getTransaction().begin();
		msg = em.merge(msg);
		em.flush();
		em.getTransaction().commit();
		em.close();
		//TODO send a message, which can be used to intercept this in panels that use websocket
		User u = msg.getRecipient();
		if (initialId == 0 && notify) {
			MessageCreatedPushMessage message = new MessageCreatedPushMessage(msg);

			UserService.get().pushMessageToUser(msg.getRecipient(), message);
			if (!msg.getSender().equals(msg.getRecipient()))
				UserService.get().pushMessageToUser(msg.getSender(), message);
		}
		return msg;
	}

	public MessageReadReceipt saveMetadata(MessageReadReceipt data) {
		EntityManager em = Simple.getEm();
		em.getTransaction().begin();
		data = em.merge(data);
		em.flush();
		em.getTransaction().commit();
		em.close();
		return data;
	}

	public long getMessageCount() {
		User u = SecurityContext.get().getUser();
		EntityManager em = Simple.getEm();
		Query q = em.createQuery("SELECT count(x) FROM Message x WHERE (x.recipient=:user OR x.sender=:user) AND x.messageThreadRoot is null").setParameter("user", u);
		Number result = (Number) q.getSingleResult();
		return result.intValue();
	}

	public long getUnreadMessageCount() {
		User u = SecurityContext.get().getUser();
		EntityManager em = Simple.getEm();
		Query q = em.createQuery("SELECT count(x) FROM MessageReadReceipt x WHERE (x.user=:user) AND x.message.messageThreadRoot is null").setParameter("user", u);
		Number result = (Number) q.getSingleResult();
		return getMessageCount() - result.intValue();
	}

	public List<Message> getMessages(User u, long page, long page_size, String order, String direction) {
		EntityManager em = Simple.getEm();
		u = em.find(User.class, u.id);
		Query q = em.createQuery("SELECT x FROM Message x WHERE (x.recipient=:user OR x.sender=:user)  AND x.messageThreadRoot is null ORDER BY _order _direction"
				.replace("_order", order)
				.replace("_direction", direction)).setParameter("user", u);
		q.setMaxResults((int) page_size);
		q.setFirstResult((int) (page * page_size));
		List list = q.getResultList();
		em.close();
		return list;
	}

	public List getNewMessages(User u, Date lastKnownMessage) {
		EntityManager em = Simple.getEm();
		u = em.find(User.class, u.id);
		Query q = em.createQuery("SELECT x FROM Message x WHERE x.timeSent > :lastKnown")
				.setParameter("lastKnown", lastKnownMessage);
		List list = q.getResultList();
		em.close();
		return list;
	}

	public void deleteMessage(Message message) {
		EntityManager em = Simple.getEm();

		if (message == null) {
			throw new NullPointerException("Message can not be null");
		}
		List<Message> thread = getThread(message);

		em.getTransaction().begin();
		for (Message m : thread) {

			m = em.find(Message.class, m.getId());
			em.remove(m);
		}
		em.flush();
		em.getTransaction().commit();
		em.close();

	}

	public List<Message> getThread(Message m) {
		EntityManager em = Simple.getEm();
		Message root;
		if (m.getMessageThreadRoot() != null) {
			root = m.getMessageThreadRoot();
		} else {
			root = m;
		}

		final Query q = em.createQuery("select distinct M FROM Message M WHERE M.messageThreadRoot.id=:message_id").setParameter("message_id", root.id);
		List<Message> list = q.getResultList();
		ArrayList<Message> thread = new ArrayList();
		thread.addAll(list);

		if (m == root) {
			thread.add(m);
		} else {
			thread.add(root);

		}
		Collections.sort(thread, new Comparator<Message>() {
			@Override
			public int compare(Message o1, Message o2) {
				return o1.getTimeSent().compareTo(o2.getTimeSent());
			}
		});
		return thread;
	}

	public Message findMessage(long message_id) {
		return Simple.getEm().find(Message.class, message_id);
	}

	public Message replyMessage(Message m, String response) {
		User sender = m.getSender() != null ? m.getSender()
				: User.fromContactInfo(SecurityContext.get().getUser().getOrganization(), m.getSenderContactInfo());
		if (m.getSender() == null) {

			m.setSender(sender);

			m = MessagingService.get().saveMessage(m, true);
		}

		Message replyMessage = new Message(m);
		//replyMessage.setRecipient(sender);
		replyMessage.setBody(response);
		replyMessage.setSender(SecurityContext.get().getUser());
		replyMessage = MessagingService.get().saveMessage(replyMessage, true);

		//Notify users in thread

		if (m.getMessageThreadRoot() != null)
			m = m.getMessageThreadRoot();
		List<User> users = Lists.newArrayList(m.getRecipient(), m.getSender());

		for (User user : users) {
			UserService.get().pushMessageToUser(user, new MessageThreadUpdatedMessaged(m));
		}
		return replyMessage;
	}

	public boolean hasBeenRead(Message message, User user) {
		User u = SecurityContext.get().getUser();
		EntityManager em = Simple.getEm();
		message = findMessage(message.id);
		Query q = em.createQuery("SELECT count(x) FROM MessageReadReceipt x WHERE (x.user=:user AND x.message=:msg)").setParameter("user", u).setParameter("msg", message);
		Number result = (Number) q.getSingleResult();
		return (result.intValue() > 0);
	}

	public void markAsRead(Message message, User user) {
		checkNotNull(message);
		checkNotNull(message);
		if (!hasBeenRead(message, user)) {
			MessageReadReceipt receipt = new MessageReadReceipt();
			receipt.setUser(user);
			receipt.setMessage(message);
			saveMetadata(receipt);
		}

	}

	public Message getSupportThread() {
		User from = SecurityContext.get().getUser();
		User to = UserService.get().getUser("admin");
		Message m = findMessage(from, to, SUPPORT_THREAD);
		if (m == null) {
			m = new Message();
			m.setTitle(SUPPORT_THREAD);
			m.setBody("");
			m.setSender(from);
			m.setRecipient(to);
			m = MessagingService.get().saveMessage(m, false);
			MessagingService.get().markAsRead(m, to);
			MessagingService.get().markAsRead(m, from);
		}
		return m;
	}

	private Message findMessage(User from, User to, String s) {
		EntityManager em = Simple.getEm();
		final Query q = em.createQuery("select distinct M FROM Message M WHERE M.sender=:sender AND M.recipient=:recipient AND M.title=:title")
				.setParameter("sender", from)
				.setParameter("recipient", to)
				.setParameter("title", s);
		List<Message> list = q.getResultList();
		em.close();
		if (list.size() > 0) {
			return list.get(0);
		}
		return null;

	}

	public List<Message> getMessages(User u) {
		EntityManager em = Simple.getEm();
		u = em.find(User.class, u.id);
		Query q = em.createQuery("SELECT x FROM Message x WHERE (x.recipient=:user OR x.sender=:user)");
		q.setParameter("user", u);
		List list = q.getResultList();
		em.close();
		return list;
	}
}
