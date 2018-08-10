package com.mysaasa.core.database.services;

import com.mysaasa.MySaasa;
import com.mysaasa.interfaces.annotations.SimpleService;

/**
 * Created by adam on 14-12-12.
 */
@SimpleService
public class PropertyService {
	public static PropertyService get() {
		return MySaasa.getInstance().getInjector().getProvider(PropertyService.class).get();
	}
}
