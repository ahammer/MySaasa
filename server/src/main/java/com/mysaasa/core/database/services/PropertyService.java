package com.mysaasa.core.database.services;

import com.mysaasa.interfaces.annotations.SimpleService;
import com.mysaasa.Simple;

/**
 * Created by adam on 14-12-12.
 */
@SimpleService
public class PropertyService {
	public static PropertyService get() {
		return Simple.getInstance().getInjector().getProvider(PropertyService.class).get();
	}
}
