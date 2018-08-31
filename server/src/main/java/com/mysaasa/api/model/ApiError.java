package com.mysaasa.api.model;

import com.mysaasa.server.ErrorCode;
import org.apache.commons.lang.exception.ExceptionUtils;

/**
 * When the API has an error, we return this.
 *
 *
 * T Any class that plays nice with ApiSerializable (should be a bulk of them, but it's intended for simple data objects.
 *
 * Created by administrator on 3/15/2014.
 */
public class ApiError<T> extends ApiResult<T> {
	public final String errorcode;
	public final String stacktrace;

	public ApiError(Exception e, ErrorCode code) {
		super(e);
		message = e.getMessage();
		this.errorcode = code.name();
		this.stacktrace = ExceptionUtils.getStackTrace(e);
	}

	public ApiError(Exception e) {
		super(e);
		message = e.getMessage();
		this.errorcode = ErrorCode.UNKNOWN_ERROR.name();
		this.stacktrace = ExceptionUtils.getStackTrace(e);
	}
}
