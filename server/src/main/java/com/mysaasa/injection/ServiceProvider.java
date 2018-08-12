package com.mysaasa.injection;

import com.google.inject.Provider;

/**
 * Guice Provider for a IService Created by Adam on 3/30/14.
 */
public class ServiceProvider<T> implements Provider<T> {
	T instance;

	public ServiceProvider(T instance) {
		this.instance = instance;
	}

	@Override
	public T get() {
		return instance;
	}
}
