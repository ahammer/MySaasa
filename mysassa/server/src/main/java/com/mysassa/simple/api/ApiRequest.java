package com.mysassa.simple.api;

import com.mysassa.simple.SimpleImpl;

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
			Object obj = SimpleImpl.get().getInjector().getProvider(
					apiMapping.getMethod().getDeclaringClass())
					.get();
			return (ApiResult<?>) apiMapping.getMethod().invoke(obj, parameters);
		} catch (IllegalAccessException e) {
			return new ApiError("Bad, you do not have permission");
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			return new ApiError("Usually, this means you are not on a website or the function crashed " + e.getLocalizedMessage());
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			return new ApiError(e.getLocalizedMessage() + " " + apiMapping.getMethod().getParameterTypes().length + " = " + parameters.length);
		}
	}
}
