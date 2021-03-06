package com.mysaasa.core.users.service;

import com.google.android.gcm.server.Sender;
import com.google.gson.Gson;
import com.mysaasa.DefaultPreferences;
import com.mysaasa.MySaasa;
import com.mysaasa.core.users.model.GcmKey;
import com.mysaasa.core.users.model.User;
import com.mysaasa.interfaces.annotations.SimpleService;
import com.mysaasa.messages.SimpleWebSocketPushMessage;
import com.mysaasa.core.messaging.model.Message;
import com.mysaasa.core.organization.model.Organization;
import com.mysaasa.core.security.PasswordHash;
import org.apache.wicket.Application;
import org.apache.wicket.protocol.ws.WebSocketSettings;
import org.apache.wicket.protocol.ws.api.IWebSocketConnection;
import org.apache.wicket.protocol.ws.api.registry.IKey;
import org.apache.wicket.protocol.ws.api.registry.IWebSocketConnectionRegistry;

import javax.inject.Inject;
import javax.mail.*;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;

@SimpleService
public class UserService {

	@Inject
	EntityManager em;

	private final Gson gson = new Gson();

	public static UserService get() {
		return MySaasa.getInstance().getInjector().getProvider((UserService.class)).get();
	}

	public final Map<User, List<UserWebsocketEntry>> WebsocketUserRegistry = new HashMap();

	public final Sender gcmSender = new Sender(DefaultPreferences.getProperties().getProperty("GCM.KEY", ""));

	private boolean gcmEnabled() {
		return DefaultPreferences.getProperties().containsKey("GCM.KEY");
	}

	public void RegisterUserGcm(User u, String gc_reg_id) {
		checkNotNull(gc_reg_id);
		u.addGcmKey(new GcmKey(gc_reg_id));
		UserService.get().saveUser(u);
	}

	public Message findMessageById(long id) {
		return em.find(Message.class, id);
	}

	private static class UserWebsocketEntry {
		public final IKey key;
		public final String session;

		@Override
		public boolean equals(Object o) {
			if (this == o)
				return true;
			if (o == null || getClass() != o.getClass())
				return false;

			UserWebsocketEntry that = (UserWebsocketEntry) o;

			if (key != null ? !key.equals(that.key) : that.key != null)
				return false;
			if (session != null ? !session.equals(that.session) : that.session != null)
				return false;

			return true;
		}

		@Override
		public int hashCode() {
			int result = key != null ? key.hashCode() : 0;
			result = 31 * result + (session != null ? session.hashCode() : 0);
			return result;
		}

		private UserWebsocketEntry(IKey key, String session) {
			this.key = key;
			this.session = session;

		}
	}

	public void RegisterUserWebsocket(User u, IKey key, String session) {
		List<UserWebsocketEntry> list = WebsocketUserRegistry.get(u);
		if (list == null) {
			list = new ArrayList();
		}

		UserWebsocketEntry entry = new UserWebsocketEntry(key, session);
		list.add(entry);
		WebsocketUserRegistry.put(u, list);
	}

	public void UnregisterUserWebsocket(User u, IKey key, String session) {
		UserWebsocketEntry entry = new UserWebsocketEntry(key, session);
		List<UserWebsocketEntry> list = WebsocketUserRegistry.get(u);
		if (list != null) {
			list.remove(entry);
		}
	}

	public void pushMessageToUser(User u, SimpleWebSocketPushMessage message) {
		if (u == null)
			return;
		List<UserWebsocketEntry> entries = WebsocketUserRegistry.get(u);
		Application application = MySaasa.getInstance();
		WebSocketSettings webSocketSettings = WebSocketSettings.Holder.get(application);
		IWebSocketConnectionRegistry webSocketConnectionRegistry = webSocketSettings.getConnectionRegistry();
		if (entries != null)
			for (UserWebsocketEntry entry : entries) {
				IWebSocketConnection connection = webSocketConnectionRegistry.getConnection(application, entry.session, entry.key);
				if (connection != null && connection.isOpen()) {
					connection.sendMessage(message);
				}
			}

		if (u.getGcmKeys() != null)
			for (GcmKey gcmKey : u.getGcmKeys()) {
				if (gcmKey.getKey() != null) {
					System.out.println("Sending to user: " + u + " Message: " + message + " " + gcmKey.getKey());

					com.google.android.gcm.server.Message m = new com.google.android.gcm.server.Message.Builder()
							.addData("class", message.getPushMessage())
							.addData("data", gson.toJson(message.getData()))
							.build();

					try {
						gcmSender.sendNoRetry(m, gcmKey.getKey());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
	}

	public UserService() {
		super();
	}

	public void disableUser(User u) {
		u.setEnabled(false);
		u = saveUser(u);
		System.out.println("Disabled: " + u);
	}

	/**
	 * Saves a user, this may be new or a edit, however create users with createUser instead of saveUser
	 * 
	 * @param user
	 *            the user we are saving
	 * @return the saved/tracked user
	 */
	public User saveUser(final User user) {
		em.getTransaction().begin();
		boolean newUser = false;
		if (user.id == 0 && user.getContactInfo() != null && user.getContactInfo().getEmail() != null) {
			newUser = true;
		}
		final User user2 = em.merge(user);
		em.flush();
		em.getTransaction().commit();

		if (newUser && user2.getContactInfo().getEmail() != null && !user2.getContactInfo().getEmail().trim().equals("")) {
			try {
				MailService.get().sendWelcomeEmail(user2.getContactInfo().getEmail(), user2);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return user2;
	}

	/**
	 * Creates a User,
	 *
	 * Throws if the User Exists or is invalid
	 * 
	 * @param identifier
	 *            user id
	 * @param password
	 *            user password
	 * @param organization
	 *            the users organization
	 * @return The user that was created
	 */
	public User createUser(String identifier, String password, Organization organization) {
		checkNotNull(identifier);
		checkNotNull(password);
		checkNotNull(organization);

		if (password.length() <= 4)
			throw new IllegalArgumentException("Password is to short, less than 4 chars");
		if (userExists(identifier)) {
			throw new IllegalStateException("Already a user at this identifier");
		}
		;

		User u = new User(identifier, password, User.AccessLevel.GUEST);
		u.setOrganization(organization);
		u.setIdentifier(identifier);
		u.setPassword_md5(PasswordHash.createHash(password));
		u = saveUser(u);
		return u;
	}

	public User findUser(String identifier, String password) throws UserDisabledException {
		final Query q = em.createQuery("SELECT U FROM User U WHERE UPPER(U.identifier)=:identifier");
		q.setParameter("identifier", identifier.toUpperCase());
		@SuppressWarnings("unchecked")
		final List<User> list = q.getResultList();
		if (list.size() > 0) {
			for (User u : list) {
				try {
					if (PasswordHash.validatePassword(password, u.getPassword_md5())) {
						if ((u.getEnabled() == false) || u.organization.isEnabled() == false)
							throw new UserDisabledException();
						return u;
					}
				} catch (NoSuchAlgorithmException e) {

				} catch (InvalidKeySpecException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	public boolean userExists(String identifier) {
		checkNotNull(identifier);
		final Query q = em.createQuery("SELECT U FROM User U WHERE UPPER(U.identifier)=:identifier");
		q.setParameter("identifier", identifier.toUpperCase());
		final List<User> list = q.getResultList();

		if (list.size() == 1) {
			return true;
		} else if (list.size() > 1) {
			throw new RuntimeException("Duplicate Identifier Found: " + identifier);
		}
		return false;
	}

	public User findUserByEmail(String email) {
		final Query q = em.createQuery("SELECT U FROM User U WHERE U.contactInfo.email=:identifier");
		q.setParameter("identifier", email);
		@SuppressWarnings("unchecked")
		final List<User> list = q.getResultList();
		if (list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	public boolean provideEmailAccess(String identifier) {

		if (userExists(identifier)) {
			User u = getUser(identifier);

			try {
				MailService.get().sendNonceEmail(u);
				return true;
			} catch (MessagingException e) {
				e.printStackTrace();
				return false;
			}

		}
		return false;
	}

	public User findUserById(long id) {
		final Query q = em.createQuery("SELECT U FROM User U WHERE U.id=:id");
		q.setParameter("id", id);
		@SuppressWarnings("unchecked")
		final List<User> list = q.getResultList();
		if (list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	public int getUserCount() {
		Query q = em.createQuery("SELECT count(x) FROM User U WHERE " + "(U.enabled!=FALSE or U.enabled IS NULL) AND (U.organization.enabled!=FALSE or U.organization.enabled IS NULL)");
		Number result = (Number) q.getSingleResult();
		return result.intValue();
	}

	public List<User> getUsers(Organization organization) {
		checkNotNull(organization);
		List<User> results = em.createQuery("SELECT U FROM User U WHERE U.organization=:organization AND" + "((U.enabled!=FALSE or U.enabled IS NULL) AND (U.organization.enabled!=FALSE or U.organization.enabled IS NULL))").setParameter("organization", organization)
				.getResultList();

		return results;
	}

	public List<User> getAllUsers() {
		List<User> results = em.createQuery("SELECT U FROM User U WHERE " + "(U.enabled!=FALSE or U.enabled IS NULL) AND (U.organization.enabled!=FALSE or U.organization.enabled IS NULL)")
				.getResultList();

		return results;
	}

	public User getUser(String identifier) {
		final Query q = em.createQuery("SELECT U FROM User U WHERE UPPER(U.identifier)=:identifier");
		q.setParameter("identifier", identifier.toUpperCase());
		final List<User> list = q.getResultList();

		if (list.size() == 1) {
			return list.get(0);
		} else if (list.size() > 1) {
			throw new RuntimeException("Duplicate Identifier Found: " + identifier);
		}
		return null;
	}

}
