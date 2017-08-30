package com.mysaasa.core.users.service;

import com.mysaasa.api.model.ApiSuccess;
import com.mysaasa.core.users.model.User;
import com.mysaasa.interfaces.annotations.SimpleService;
import com.mysaasa.api.model.ApiError;
import com.mysaasa.api.ApiNotAuthorized;
import com.mysaasa.api.model.ApiResult;
import com.mysaasa.core.security.services.SecurityService;
import com.mysaasa.core.security.services.session.SecurityContext;

/**
 * Created by Adam on 3/15/14.
 *
 *
 */

@SimpleService
public class UserTemplateServiceImpl extends UserTemplateService {
	@Override
	public ApiResult<String> createUser(String identifier, String password) {
		return UserApiService.get().createUser(identifier, password);
	}

	@Override
	public ApiResult<String> loginUser(String identifier, String password) {
		return UserApiService.get().loginUser(identifier, password);
	}

	@Override
	public ApiResult getSession() {
		return UserApiService.get().getSession();
	}

	@Override
	public ApiResult logout() {
		return UserApiService.get().logout();

	}

	@Override
	public ApiResult generateNonce() {
		try {
			User u = SecurityContext.get().getUser();
			if (u == null)
				return new ApiNotAuthorized();
			return new ApiSuccess(SecurityService.get().generateNonce());
		} catch (Exception e) {
			return new ApiError(e);
		}

	}
}
