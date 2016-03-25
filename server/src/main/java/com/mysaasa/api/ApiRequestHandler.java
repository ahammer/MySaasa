package com.mysaasa.api;

import org.apache.wicket.request.IRequestCycle;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.http.WebResponse;

/**
 * Created by Adam on 3/12/14.
 */
public class ApiRequestHandler implements IRequestHandler {
	final ApiRequest apiRequest;

	public ApiRequestHandler(ApiRequest ApiRequest) {
		this.apiRequest = ApiRequest;

	}

	@Override
	public void respond(IRequestCycle requestCycle) {
		ApiResult<?> result = apiRequest.invoke();
		result.toJson();
		final WebResponse response = (WebResponse) requestCycle.getResponse();
		final byte[] data = result.toJson().getBytes();
		response.setContentLength(data.length);
		response.write(data);
	}

	@Override
	public void detach(IRequestCycle iRequestCycle) {

	}

}
