package com.mysaasa.api;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

/**
 * This maps a Method with a list of expected parameters and names names must be manually annotated because they are not available at run time via reflection
 */
public class ApiMapping implements Serializable {
	private final List<ApiParameter> parameters = new ArrayList<>();
	private final Method method;

	public ApiMapping(Method m) {

		this.method = m;
		try {
			Parameter[] names = m.getParameters();

			int pos = 0;
			for (Parameter p : names) {
				if (!(p.getType().isPrimitive() || p.getType().equals(String.class)))
					throw new IllegalArgumentException("Primitive or string required for Api Call Arguments!! Problem Param: " + p + " " + " method:" + m);
				getParameters().add(new ApiParameter(p));
				pos++;
			}
		} catch (NullPointerException e) {
			throw e;
		}
	}

	public List<ApiParameter> getParameters() {
		return parameters;
	}

	public Method getMethod() {
		return method;
	}

	@Override
	public String toString() {
		return "ApiMapping{" + "parameters=" + parameters + ", method=" + method + '}';
	}

}
