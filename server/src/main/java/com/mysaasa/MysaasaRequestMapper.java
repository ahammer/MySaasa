package com.mysaasa;

import com.mysaasa.api.ApiHelperService;
import com.mysaasa.api.ApiRequestHandler;
import com.mysaasa.core.website.model.Website;
import com.mysaasa.core.website.templating.MediaRequestHandler;
import com.mysaasa.core.hosting.service.HostingService;
import com.mysaasa.core.website.templating.TemplatedSiteRequestHandler;
import org.apache.velocity.app.VelocityEngine;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.IRequestMapper;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Url;

/**
 * Map's a request, basically if it's for a Website it has a high compatibility.
 * If it's for a website it's got a prioritized priority
 *
 * this allows fallback to bundled resources globally on all domains and 404 handling by jetty

* Created by Adam on 2/13/14.
*/
public class MysaasaRequestMapper implements IRequestMapper {
	public static final String CANCEL_SESSION_LINK = "CancelSessionLink";
	public static final int NO_MATCH = Integer.MIN_VALUE;
	public static final int MATCHING_SCORE = Integer.MIN_VALUE + 2;
	private final VelocityEngine mEngine;

	public MysaasaRequestMapper() {
		mEngine = new VelocityEngine();
	}

	/**
	 * If the website can be found, say we are compatible by returning MIN+2;
	 * If a website can't be found, INTEGER_MIN_VALUE (not compatible)
	 *
	 * TODO: It looks like we can have one RequestMapper for each type
	 * Media/QR/Template/API
	 * @param request
	 * @return
	 */
	@Override
	public int getCompatibilityScore(Request request) {
		HostingService service = HostingService.get();
		Website website = service.findWebsite(request.getUrl());
		return (website == null) ? NO_MATCH : MATCHING_SCORE;
	}

	@Override
	public Url mapHandler(IRequestHandler requestHandler) {
		return null;
	}

	long timeMillis = System.nanoTime();
	long deltaTime = 0;
	static long Requests = 0l;

	@Override
	public IRequestHandler mapRequest(Request request) {
		long currentTime = System.nanoTime();
		long tmpDeltaTime = currentTime - timeMillis;
		deltaTime = (deltaTime * 200 + tmpDeltaTime) / 201l;
		timeMillis = currentTime;

		//System.out.println("Request #"+(Requests++)+ " Request Time: " + (int)(1.0/(deltaTime/1000000000.0)) + " Requests/Second");
		String path = request.getClientUrl().getPath();
		if (path.startsWith("media/")) {
			return new MediaRequestHandler(request);
		}

		if (path.startsWith("qr/")) {
			return new QrGenerator(request);
		}

		/**
		 * If the Template Handler Likes it
		 */
		if ((TemplatedSiteRequestHandler.IsValidRequest(request))) {
			return new TemplatedSiteRequestHandler(mEngine, request);
		}

		if (path.contains("/")) {
			ApiHelperService apiHelperService = ApiHelperService.get();
			if (apiHelperService.isApiPathBound(path)) {
				return new ApiRequestHandler(apiHelperService.getApiRequest(request));
			}
		}
		return null;
	}
}
