package com.mysaasa.api;

import com.mysaasa.SimpleImpl;
import com.mysaasa.api.model.ApiError;
import com.mysaasa.api.model.ApiResult;

import java.lang.reflect.InvocationTargetException;

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
public class ApiRequest {
	final ApiMapping apiMapping;

	Object[] parameters;

	public ApiRequest(ApiMapping method, Object[] parameters) {
		this.apiMapping = method;
		this.parameters = parameters;
	}

	public ApiRequest(ApiMapping mapping) {
		this.apiMapping = mapping;
		this.parameters = new Object[0];

	}

	public ApiResult<?> invoke() {
		try {
			//This uses Guice to inject the relevant service and invoke on it..
			//Basically, we have a method, but no object to invoke that method on.
			//So we say, Hey method, where are you declared? Hey Guice, give me one of those
			//Now that I have the Object, let's run Method on Object with Parameters.
			Object obj = SimpleImpl.getInstance().getInjector().getProvider(
					apiMapping.getMethod().getDeclaringClass())
					.get();
			return (ApiResult<?>) apiMapping.getMethod().invoke(obj, parameters);
		} catch (IllegalAccessException e) {
			return new ApiError(e);
		} catch (InvocationTargetException e) {
			return new ApiError(e);
		} catch (IllegalArgumentException e) {
			return new ApiError(e);
		}
	}
}
