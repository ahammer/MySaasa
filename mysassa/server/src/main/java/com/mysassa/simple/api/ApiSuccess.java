package com.mysassa.simple.api;

/**
 * A successful message
 * Created by administrator on 3/15/2014.
 */
public class ApiSuccess<T> extends ApiResult<T> {

	public ApiSuccess(T s) {
		super(s);
		success = true;
	}

}
