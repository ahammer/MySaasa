package com.mysaasa.core.hosting.service;

import com.google.inject.Injector;
import com.mysaasa.MySaasa;

/**
 * These classes can be injected via annotation
 */
public class BaseInjectedService {
	public final void inject() {
	    MySaasa.inject(this);
	}
}
