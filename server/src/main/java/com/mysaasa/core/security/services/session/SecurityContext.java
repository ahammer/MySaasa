package com.mysaasa.core.security.services.session;

import com.google.gson.annotations.Expose;
import com.mysaasa.SimpleImpl;
import com.mysaasa.core.organization.model.Organization;
import com.mysaasa.core.users.model.User;
import com.mysaasa.core.website.model.Website;
import com.mysaasa.core.AbstractModule;
import com.mysaasa.core.ModuleManager;
import com.mysaasa.core.security.services.SessionService;
import org.apache.commons.collections.ListUtils;
import org.apache.wicket.Session;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
* Created by adam on 2014-09-26.
*/
public class SecurityContext implements Serializable {
	@Expose
	private final User user;

	public boolean nonce_signin = false;

	public SecurityContext(User user) {
		this.user = user;
	}

	public User getUser() {
		return user;
	}

	public List<AbstractModule> getAvailableModules() {
		List<AbstractModule> mModules = new ArrayList<>();

		ModuleManager moduleManager = SimpleImpl.get().getInjector().getProvider(ModuleManager.class).get();
		for (AbstractModule module : moduleManager.getModules()) {
			if (module.hasAccess(getUser().getAccessLevel())) {
				mModules.add(module);
			}
		}
		return mModules;
	}

	public List<Website> getWebsites() {
		switch (getUser().getAccessLevel()) {
			//case ROOT:
			//			return HostingService.get().getWebsites();
		case GUEST:
			return ListUtils.EMPTY_LIST;

		default:
			Organization o = getUser().findOrganization();
			return o.retrieveWebsites();
		}
	}

	public boolean canCreateWebsite() {
		switch (getUser().getAccessLevel()) {
		case ROOT:
		case ORG:
			return true;
		default:
			return false;
		}

	}

	public static SecurityContext get() {
		return SessionService.get().getSecurityContext(Session.get());
	}
}
