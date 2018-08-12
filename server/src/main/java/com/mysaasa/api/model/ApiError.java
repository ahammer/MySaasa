package com.mysaasa.api.model;

/**
 * When the API has an error, we return this.
 *
 *
 * T Any class that plays nice with ApiSerializable (should be a bulk of them, but it's intended for simple data objects.
 *
 * Created by administrator on 3/15/2014.
 */
public class ApiError<T> extends ApiResult<T> {

	static final String NPE_ERROR_MESSAGE = "Server Error: NullPointerException";

	public ApiError(Exception e) {
		super(e);
		if (e instanceof NullPointerException) {
			message = NPE_ERROR_MESSAGE;
		} else {
			message = e.toString();
		}
	}
}
