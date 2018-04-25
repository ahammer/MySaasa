package com.mysaasa.core.hosting.service;

import com.mysaasa.Simple;

/**
 * These classes can be injected via the
 * @Inject annotation
 * 
 */
public class BaseInjectedService {
	public final void inject() {
		Simple.getInstance().getInjector().injectMembers(this);
	}

}
