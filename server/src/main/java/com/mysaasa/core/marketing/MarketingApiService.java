package com.mysaasa.core.marketing;

import com.mysaasa.api.model.ApiError;
import com.mysaasa.api.model.ApiResult;
import com.mysaasa.api.model.ApiSuccess;
import com.mysaasa.core.security.services.session.SecurityContext;
import com.mysaasa.interfaces.IApiService;
import com.mysaasa.interfaces.annotations.ApiCall;
import com.mysaasa.interfaces.annotations.SimpleService;

import javax.inject.Inject;

@SimpleService
public class MarketingApiService implements IApiService {
	@Inject
	MarketingService service;

	@ApiCall
	public ApiResult addReferral(long parentId, long childId) {
		try {
			service.addReferral(parentId, childId);
			return new ApiSuccess(true);
		} catch (Exception e) {
			return new ApiError(e);
		}
	}

	@ApiCall
	public ApiResult getUserReferralData() {
		return new ApiSuccess(service.findReferral(SecurityContext.get().getUser().id));
	}
}
