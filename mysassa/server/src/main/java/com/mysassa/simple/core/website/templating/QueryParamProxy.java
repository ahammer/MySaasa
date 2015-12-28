package com.mysassa.simple.core.website.templating;

import com.mysassa.simple.api.ApiSuccess;
import org.apache.wicket.request.IRequestParameters;

/**
 * Proxies iRequestParameters for the template
 * Created by Adam on 3/18/14.
 */
public class QueryParamProxy extends ApiSuccess {
	private final IRequestParameters queryParameters;

	public QueryParamProxy(IRequestParameters queryParameters) {
		super("");
		this.queryParameters = queryParameters;
	}

	public String get(String name) {
		return queryParameters.getParameterValue(name).toString();
	}

	public Long getAsLong(String name) {
		return queryParameters.getParameterValue(name).toLong();
	}

	public Integer getAsInt(String name) {
		return queryParameters.getParameterValue(name).toInteger();
	}

	public boolean contains(String name) {
		return queryParameters.getParameterNames().contains(name);
	}

	@Override
	public String toString() {
		String result = "";
		for (String s : queryParameters.getParameterNames()) {
			result += s + " = " + queryParameters.getParameterValue(s) + "\n";
		}
		return result;
	}

	public IRequestParameters getQueryParameters() {
		return queryParameters;
	}
}
