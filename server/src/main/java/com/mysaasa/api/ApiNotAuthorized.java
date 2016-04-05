package com.mysaasa.api;

import com.google.gson.annotations.Expose;

/**
 * Created by Adam on 1/11/2015.
 */
public class ApiNotAuthorized extends ApiError {
	@Expose
	final boolean authorizationFailure = true;

	public ApiNotAuthorized() {
		super(new SecurityException("No User Signed In"));
	}
}
