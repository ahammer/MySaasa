package com.mysassa.core.security.services.session;

import com.mysassa.core.security.services.SessionService;
import com.mysassa.core.website.model.Website;
import com.mysassa.core.hosting.service.HostingService;
import org.apache.wicket.Session;
import org.apache.wicket.request.cycle.RequestCycle;

import java.io.Serializable;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * This is the Current AdminSession, it may be registered against a Templates session as well, for theming.
 *
* Created by adam on 2014-09-26.
*/
public class AdminSession implements Serializable {

	public static enum Environment {
		Staging, Live
	}

	private final SecurityContext context;
	@Deprecated
	private Website website;
	private Website theme;
	private Environment env = Environment.Live;
	private boolean editMode = true; //Inline editor in edit mode
	private boolean newPostAllowed = true; //Inline editor allow new post?

	public AdminSession(SecurityContext context) {
		this.context = context;
	}

	public SecurityContext getContext() {
		return context;
	}

	public Environment getEnv() {
		return env;
	}

	public void setEnv(Environment env) {
		this.env = env;
	}

	public boolean getEditMode() {
		return editMode;
	}

	public void setEditMode(boolean editMode) {
		this.newPostAllowed = editMode; //Let's couple these
		this.editMode = editMode;
	}

	public boolean isNewPostAllowed() {
		return newPostAllowed;
	}

	public void setNewPostAllowed(boolean newPost) {
		this.newPostAllowed = newPost;
	}

	public void setTheme(Website theme) {
		this.theme = theme;
	}

	public Website getTheme() {
		return theme;
	}

	@Override
	public String toString() {
		return "AdminSession{" + "  theme=" + theme + ", env=" + env + ", editMode=" + editMode + '}';
	}

	@Deprecated
	public void setWebsite(Website website) {
		this.website = website;
	}

	@Deprecated
	public Website getWebsite() {
		return website;
	}

	/**
	 * Shortcut
	 *
	 * @return
	 */
	public static AdminSession get() {
		RequestCycle requestCycle = RequestCycle.get();

		String host = requestCycle.getRequest().getClientUrl().getHost();
		String session_part = null;

		if (HostingService.isSessionLinked(host)) {
			String real_domain = HostingService.RealDomain(host);
			session_part = HostingService.Session(host);
			host = real_domain;
		}

		if (session_part != null) {
			return SessionService.get().getAdminSession(SessionService.get().findSessionById(session_part));
		}

		return SessionService.get().getAdminSession(Session.get());
	}

}
