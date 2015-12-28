package com.mysassa.simple.api;

/**
 * When the API has an error, we return this.
 *
 *
 * T            Any class that plays nice with ApiSerializable (should be a bulk of them, but it's intended for simple data objects.
 *
 * Created by administrator on 3/15/2014.
 */
public class ApiError<T> extends ApiResult<T> {

	public ApiError(T s) {
		super(s);
		success = false;
	}

	public ApiError(Exception e) {
		super((Exception) e);
	}

}
