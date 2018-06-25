package com.mysaasa.core.marketing;

import com.mysaasa.api.model.ApiResult;
import com.mysaasa.api.model.ApiSuccess;
import com.mysaasa.interfaces.IApiService;
import com.mysaasa.interfaces.annotations.ApiCall;
import com.mysaasa.interfaces.annotations.SimpleService;

@SimpleService
public class MarketingApiService implements IApiService {
	@ApiCall
	public ApiResult test() {
		return new ApiSuccess("Success");
	}

}
