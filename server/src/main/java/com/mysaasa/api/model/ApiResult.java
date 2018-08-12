package com.mysaasa.api.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.mysaasa.interfaces.IApiSerializable;

/**
 * <p>
 * This class wraps a message of any type.
 * </p>
 * <p>
 * This is the default framework for a API response message, success or failure. It wraps any type object, and then simply uses lazy GSON to serialize it to json.
 * </p>
 * Created by Adam on 3/12/14.
 */
public abstract class ApiResult<T> implements IApiSerializable {
	@Expose
	String message;

	// success for success.
	@Expose
	boolean success;

	@Expose
	public final T data;

	public ApiResult(Exception e) {
		message = e.getMessage();
		data = null;
	}

	public ApiResult(T s) {
		message = "ok";
		data = s;
	}

	@Override
	public String toJson() {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		String json = gson.toJson(this);
		return json;
	}

	public boolean isSuccess() {
		return success;
	}

	public T getData() {
		return data;
	}

	@Override
	public String toString() {
		return toJson();
	}

}
