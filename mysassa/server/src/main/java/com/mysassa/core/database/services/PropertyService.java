package com.mysassa.core.database.services;

import com.mysassa.interfaces.annotations.SimpleService;
import com.mysassa.Simple;

/**
 * Created by adam on 14-12-12.
 */
@SimpleService
public class PropertyService {
	public static PropertyService get() {
		return Simple.get().getInjector().getProvider(PropertyService.class).get();
	}
}
