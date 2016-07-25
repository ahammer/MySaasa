package com.mysaasa.core.security.services;

import com.google.inject.Injector;
import com.mysaasa.core.security.services.session.SecurityContext;
import com.mysaasa.core.users.model.User;
import com.mysaasa.interfaces.annotations.SimpleService;
import com.mysaasa.messages.AjaxIntent;
import com.mysaasa.Simple;
import com.mysaasa.core.hosting.service.HostingService;
import com.mysaasa.core.security.services.session.AdminSession;
import com.mysaasa.messages.ACTIONS;
import org.apache.wicket.Session;
import org.apache.wicket.ajax.AjaxRequestTarget;

import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;

@SimpleService
public class SessionService {
	public static final String NAME = "SecurityService";

	//These fields are for the current local Session
	private Session currentLocalSession;
	private User currentLocalUser;

	public String getName() {
		return NAME;
	}

	//A map of all sessions by their ID for lookup
	private final Map<String, Session> sessionMap = new HashMap<>(); //To be able to find sessions by string
	private final Map<Session, WebsiteSession> websiteSessionMap = new HashMap<>(); //A Map of Website Sessions. Contains reference to Users Cart
	private final Map<Session, SecurityContext> authenticationMap = new HashMap<>(); //A Map of Security Contexts to Sessions
	private final Map<Session, AdminSession> adminSessionMap = new HashMap<>(); //A Map of Admin Sessions

	public static SessionService get() {
		Simple s = Simple.get();
		Injector i = s.getInjector();
		return i.getProvider((SessionService.class)).get();
	}

	public SessionService() {
		super();
	}

	/**
	 * Unregister/Signout of a session, this will clear the mappings for the session
	 *
	 * @param session
	 */
	public void unregisterSession(Session session) {
		authenticationMap.remove(session);
		websiteSessionMap.remove(session);
		adminSessionMap.remove(session);
		AdminSession adminSession = adminSessionMap.get(session);
	}

	public WebsiteSession getWebsiteSession(Session mSession) {
		if (!websiteSessionMap.containsKey(mSession)) {
			mSession.bind();
			websiteSessionMap.put(mSession, new WebsiteSession());
		}
		return websiteSessionMap.get(mSession);

	}

	public SecurityContext getSecurityContext(Session mSession) {
		//When we are in Local Dev mode we will always use the last registered sess
		if (Simple.isLocalDevMode()) {
			return authenticationMap.get(currentLocalSession);
		}
		return authenticationMap.get(mSession);
	}

	/**
	 * Register a User to a Session
	 *
	 * @param mSession
	 * @param user
	 */
	public void registerUser(Session mSession, User user) {
		adminSessionMap.remove(mSession);
		websiteSessionMap.remove(mSession);
		sessionMap.remove(mSession.getId());
		authenticationMap.remove(mSession);


		SecurityContext context = createSecurityContext(user);
		sessionMap.put(mSession.getId(), mSession);
		authenticationMap.put(mSession, context);
		if (Simple.isLocalDevMode()) {
			setCurrentLocalSession(mSession);
			setCurrentLocalUser(user);
		}
	}

	public boolean hasAdminSession(String session) {

		return sessionMap.containsKey(session);
	}

	public AdminSession getAdminSession(Session session) {

		if (session == null)
			session = Session.get();
		session.bind();
		if (adminSessionMap.containsKey(session)) {
			return adminSessionMap.get(session);
		} else {
			AdminSession p = new AdminSession(authenticationMap.get(session));
			sessionMap.put(session.getId(), session);
			registerAdminSession(session, p);
			return p;
		}
	}

	//If we want to notify the system durint a ajax request that a setting has been updated
	//This is useful to reload controls that might rely on the settings.
	public void notifyAdminSessionUpdate(AjaxRequestTarget target) {
		AjaxIntent intent = new AjaxIntent(target);
		intent.setAction(ACTIONS.ACTION_USER_PREFS_UPDATED);
		intent.send();
	}

	private SecurityContext createSecurityContext(User user) {
		return new SecurityContext(user);
	}

	public Session findSessionById(String value) {
		checkNotNull(value);
		return sessionMap.get(value);
	}

	/**
	 * An AdminSession. Sometimes shared between the JPA and Website
	 * @param session
	 * @param adminSession
	 */
	public void registerAdminSession(Session session, AdminSession adminSession) {
		checkNotNull(session);
		adminSessionMap.put(session, adminSession);
	}

	public void removeAdminSession(Session session) {
		adminSessionMap.remove(session);
	}

	public AdminSession getAdminSessionFromDomain(String raw_host) {
		return adminSessionMap.get(getSessionFromDomain(raw_host));

	}

	public Session getSessionFromDomain(String raw_host) {
		String session_part = null;

		if (HostingService.isSessionLinked(raw_host)) {
			String real_domain = HostingService.RealDomain(raw_host);
			session_part = HostingService.Session(raw_host);
			raw_host = real_domain;
		}

		if (session_part != null) {
			return sessionMap.get(session_part);
		}

		return null;

	}

	public void setCurrentLocalSession(Session currentLocalSession) {
		this.currentLocalSession = currentLocalSession;
	}

	public Session getCurrentLocalSession() {
		return currentLocalSession;
	}

	public void setCurrentLocalUser(User currentLocalUser) {
		this.currentLocalUser = currentLocalUser;
	}

	public User getCurrentLocalUser() {
		return currentLocalUser;
	}
}
