package com.mysassa.simple.core.users.service;

import com.mysassa.simple.api.ApiError;
import com.mysassa.simple.api.ApiNotAuthorized;
import com.mysassa.simple.api.ApiResult;
import com.mysassa.simple.api.ApiSuccess;
import com.mysassa.simple.core.security.services.SecurityService;
import com.mysassa.simple.core.security.services.session.SecurityContext;
import com.mysassa.simple.core.users.model.User;
import com.mysassa.simple.interfaces.annotations.SimpleService;

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
