package com.mysaasa;

import com.mysaasa.api.ApiHelperService;
import com.mysaasa.api.ApiRequestHandler;
import com.mysaasa.core.website.model.Website;
import com.mysaasa.core.website.templating.MediaRequestHandler;
import com.mysaasa.core.hosting.service.HostingService;
import com.mysaasa.core.website.templating.TemplatedSiteRequestHandler;
import com.mysaasa.pages.Splash;
import org.apache.velocity.app.VelocityEngine;
import org.apache.wicket.core.request.mapper.MountedMapper;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.IRequestMapper;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.handler.EmptyRequestHandler;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.request.http.handler.ErrorCodeRequestHandler;

import java.util.logging.Level;
import java.util.logging.Logger;

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
	public static final ErrorCodeRequestHandler ERROR_CODE_REQUEST_HANDLER = new ErrorCodeRequestHandler(404);
	private final VelocityEngine mEngine;
	Logger logger = Logger.getLogger(MysaasaRequestMapper.class.getSimpleName());

	public MysaasaRequestMapper() {
		logger.log(Level.INFO, "MySaasa Request Mapper Created");
		mEngine = new VelocityEngine();
	}

	/**
	 * If the website can be found, say we are compatible by returning MIN+2;
	 * If a website can't be found, INTEGER_MIN_VALUE (not compatible)
	 *
	 * TODO: It looks like we can have one RequestMapper for each type
	 * Media/QR/Template/API
	 * @param request the request
	 * @return the score, if it should match
	 */
	@Override
	public int getCompatibilityScore(Request request) {
		/*
		HostingService service = HostingService.getInstance();
		Website website = service.findWebsite(request.getUrl());
		int score = (website == null) ? NO_MATCH : MATCHING_SCORE;
		logger.log(Level.INFO, request.getClientUrl() + " " + website + " " + score);
		*/

		return MATCHING_SCORE;
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
		String path1 = request.getClientUrl().getPath();
		long currentTime = System.nanoTime();
		long tmpDeltaTime = currentTime - timeMillis;
		deltaTime = (deltaTime * 200 + tmpDeltaTime) / 201l;
		timeMillis = currentTime;

		String path = request.getUrl().getPath();
		if (path.startsWith("media/")) {
			logger.log(Level.INFO, "Media -> " + request.getClientUrl().toString());
			return new MediaRequestHandler(request);
		}

		if (path.startsWith("qr/")) {
			logger.log(Level.INFO, "QR -> " + request.getClientUrl().toString());
			return new QrGenerator(request);
		}

		/**
		 * If the Template Handler Likes it
		 */
		if ((TemplatedSiteRequestHandler.IsValidRequest(request))) {
			logger.log(Level.INFO, "Template Handler -> " + request.getClientUrl().toString());
			return new TemplatedSiteRequestHandler(mEngine, request);
		}

		if (path.contains("/")) {
			ApiHelperService apiHelperService = ApiHelperService.get();
			if (apiHelperService.isApiPathBound(path)) {
				return new ApiRequestHandler(apiHelperService.getApiRequest(request));
			}
		}


		if (BakedInFileRequestHandler.isValidRequest(request)) {
			return new BakedInFileRequestHandler(request);
		}
		//http://gettingstarted.test:8080/simple_logo.png
		return ERROR_CODE_REQUEST_HANDLER;
	}
}
