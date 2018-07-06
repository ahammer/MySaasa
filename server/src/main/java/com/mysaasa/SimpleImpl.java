package com.mysaasa;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.mysaasa.injection.SimpleGuiceModuleImpl;

import java.sql.SQLException;

/**
 * This is the entry point for the application
 */
public class SimpleImpl extends Simple {
	private final Injector injector;

	public SimpleImpl() throws SQLException {
		injector = Guice.createInjector(simpleGuiceModule = new SimpleGuiceModuleImpl());
		simpleGuiceModule.linkServices();
		if (MySaasaDaemon.isLocalMode()) {
			super.setOfflineMode(true);
		}
	}


	@Override
	public Injector getInjector() {
		return injector;
	}

}
