package com.mysaasa.core.hosting.service;

import com.mysaasa.Simple;

/**
 * These classes can be injected via annotation
 * also may get lifecycler
 */
public class BaseInjectedService {
	public final void inject() {
		Simple.getInstance().getInjector().injectMembers(this);
	}

}
