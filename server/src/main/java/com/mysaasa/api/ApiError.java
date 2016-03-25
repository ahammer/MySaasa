package com.mysaasa.api;

import com.thoughtworks.xstream.alias.ClassMapper;

/**
 * When the API has an error, we return this.
 *
 *
 * T            Any class that plays nice with ApiSerializable (should be a bulk of them, but it's intended for simple data objects.
 *
 * Created by administrator on 3/15/2014.
 */
public class ApiError<T> extends ApiResult<T> {

	private ApiError(T s) {
		super(s);
		message = s.toString();
		success = false;
	}

	public ApiError(Exception e) {
		super((Exception) e);
		if (e instanceof NullPointerException) {
			message = "Server Error: NullPointerException";
		} else {
			message = e.toString();
		}
	}
}
