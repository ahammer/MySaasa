package com.mysaasa.core.marketing;

import com.mysaasa.api.model.ApiError;
import com.mysaasa.api.model.ApiResult;
import com.mysaasa.api.model.ApiSuccess;
import com.mysaasa.interfaces.IApiService;
import com.mysaasa.interfaces.annotations.ApiCall;
import com.mysaasa.interfaces.annotations.SimpleService;

import javax.inject.Inject;

@SimpleService
public class MarketingApiService implements IApiService {
	@Inject
	MarketingService service;

	@ApiCall
	public ApiResult test() {
		return new ApiSuccess("Success");
	}

	@ApiCall
	public ApiResult addReferral(long parentId, long childId) {
		try {
			service.addReferral(parentId, childId);
			return new ApiSuccess(true);
		} catch (Exception e) {
			return new ApiError(e);
		}
	}
}
