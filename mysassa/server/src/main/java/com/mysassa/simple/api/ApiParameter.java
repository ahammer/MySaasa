package com.mysassa.simple.api;

import java.lang.reflect.Parameter;

/**
 *
 */
public class ApiParameter {

	private final Parameter parameter;

	public ApiParameter(Parameter p) {
		this.parameter = p;
	}

	@Override
	public String toString() {
		return "Parameter{" + "_class=" + get_class() + ", name='" + getName() + '\'' + '}';
	}

	public Class get_class() {
		return parameter.getType();
	}

	public String getName() {
		return parameter.getName();
	}
}
