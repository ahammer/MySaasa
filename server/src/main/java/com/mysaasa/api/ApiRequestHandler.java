package com.mysaasa.api;

import com.mysaasa.api.model.ApiResult;
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

		final byte[] data = getResponseJson().getBytes();
		final WebResponse response = (WebResponse) requestCycle.getResponse();
		response.setContentLength(data.length);
		response.write(data);
	}

	public String getResponseJson() {
		ApiResult<?> result = apiRequest.invoke();
		return result.toJson();
	}

	@Override
	public void detach(IRequestCycle iRequestCycle) {

	}

}
