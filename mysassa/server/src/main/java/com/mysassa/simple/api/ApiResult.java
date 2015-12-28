package com.mysassa.simple.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.mysassa.simple.interfaces.IApiSerializable;

/**
 * <p>This class wraps a message of any type.</p>
 *<p> This is the default framework for a API response message, success or failure. It wraps any type object, and then simply
 * uses lazy GSON to serialize it to json.</p>
 * Created by Adam on 3/12/14.
 */
public abstract class ApiResult<T> implements IApiSerializable {
	//success for success.
	@Expose
	boolean success;

	@Expose
	private T data;

	public ApiResult(Exception e) {

		data = (T) e.getMessage();

	}

	public ApiResult(T s) {
		setData(s);
	}

	@Override
	public String toJson() {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		return gson.toJson(this);
	}

	@Override
	public String toXml() {
		throw new RuntimeException("Not Implemented");
	}

	public boolean isSuccess() {
		return success;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return toJson();
	}

}
