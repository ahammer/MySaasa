package com.mysassa.simple.api;

/**
 * This represents a Request on the API
 *
 * It invokes a Method with a set of Parameters, and returns a APIResult, it's a reflection proxy
 *
 * The API looks up the Method with the ApiHelperService based on the URL, once found, it's passed here, invoked,
 * and the api success is returned.
 *
 * The method is invoked against it's Injected
 *
 *
 * Created by Adam on 3/12/14.
 */
public class ApiRequestPreconditionFail extends ApiRequest {
	private final String data;

	public ApiRequestPreconditionFail(String data) {
		super(null, null);
		this.data = data;
	}

	public ApiResult<?> invoke() {
		return new ApiError(data);
	}
}
