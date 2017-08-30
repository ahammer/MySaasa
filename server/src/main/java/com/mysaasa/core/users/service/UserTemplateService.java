package com.mysaasa.core.users.service;

import com.mysaasa.api.model.ApiResult;
import com.mysaasa.interfaces.ITemplateService;

/**
 * Created by Adam on 3/15/14.
 */
public abstract class UserTemplateService implements ITemplateService {
	public static final String TEMPLATE_SHORT_NAME = "User";

	@Override
	public String getTemplateInterfaceName() {
		return TEMPLATE_SHORT_NAME;
	}

	public abstract ApiResult createUser(String identifier, String password);

	public abstract ApiResult loginUser(String identifier, String password);

	public abstract ApiResult getSession();

	public abstract ApiResult logout();

	public abstract ApiResult generateNonce();
}
