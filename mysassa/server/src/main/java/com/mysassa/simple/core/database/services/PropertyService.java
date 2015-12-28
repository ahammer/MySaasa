package com.mysassa.simple.core.database.services;

import com.mysassa.simple.Simple;
import com.mysassa.simple.interfaces.annotations.SimpleService;

/**
 * Created by adam on 14-12-12.
 */
@SimpleService
public class PropertyService {
	public static PropertyService get() {
		return Simple.get().getInjector().getProvider(PropertyService.class).get();
	}
}
