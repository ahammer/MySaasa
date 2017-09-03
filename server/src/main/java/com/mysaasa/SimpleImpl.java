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
	}

	public SimpleImpl(boolean b) {
		super(b);
		injector = Guice.createInjector(simpleGuiceModule = new SimpleGuiceModuleImpl());
	}

	@Override
	public Injector getInjector() {
		return injector;
	}

}
