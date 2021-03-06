package com.mysaasa.core.security.services;

import com.mysaasa.MySaasa;
import com.mysaasa.core.security.services.session.SecurityContext;
import com.mysaasa.core.users.model.User;
import com.mysaasa.interfaces.annotations.SimpleService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Adam on 1/2/2015.
 */

@SimpleService
public class SecurityService {

	Map<String, SigninNonce> securityMap = new HashMap();

	public static SecurityService get() {
		return MySaasa.getInstance().getInjector().getProvider(SecurityService.class).get();
	}

	public String generateNonce(User u) {
		SigninNonce nonce = new SigninNonce(u);
		securityMap.put(nonce.uuid, nonce);
		return nonce.uuid;
	}

	public static class SigninNonce {
		public SigninNonce() {
			u = SecurityContext.get().getUser();
		}

		public SigninNonce(User u) {
			this.u = u;
		}

		public final String uuid = UUID.randomUUID().toString();
		public final long timeCreated = System.currentTimeMillis();
		public final User u;
	}

	public String generateNonce() {
		SecurityContext context = SecurityContext.get();
		if (context != null && context.getUser() != null) {
			SigninNonce nonce = new SigninNonce();
			securityMap.put(nonce.uuid, nonce);
			return nonce.uuid;
		}
		return "";
	}

	public SigninNonce getNonce(String key) {
		return securityMap.remove(key);
	}
}
