package com.mysaasa.core.users.service;

import com.mysaasa.MySaasa;
import com.mysaasa.api.model.ApiError;
import com.mysaasa.api.ApiNotAuthorized;
import com.mysaasa.api.model.ApiResult;
import com.mysaasa.api.model.ApiSuccess;
import com.mysaasa.core.media.model.Media;
import com.mysaasa.core.security.model.SessionSummary;
import com.mysaasa.core.security.services.SessionService;
import com.mysaasa.core.security.services.session.SecurityContext;
import com.mysaasa.core.users.model.User;
import com.mysaasa.core.website.model.Website;
import com.mysaasa.interfaces.annotations.ApiCall;
import com.mysaasa.interfaces.annotations.SimpleService;
import com.mysaasa.core.hosting.service.HostingService;
import com.mysaasa.interfaces.IApiService;
import org.apache.wicket.Session;
import org.apache.wicket.request.Url;

/**
 * Created by Adam on 3/12/14.
 */
@SimpleService
public class UserApiService implements IApiService {
	public static UserApiService get() {
		return MySaasa.getInstance().getInjector().getProvider(UserApiService.class).get();
	}

	public UserApiService() {}

	@ApiCall
	public ApiResult<String> BasicTest() {
		return new ApiSuccess<String>("You just called Create User");
	}

	@ApiCall
	public ApiResult<Website> WebsiteTest() {
		return new ApiSuccess<Website>(MySaasa.getInstance().getInjector().getProvider(

		HostingService.class).get().findWebsite(Url.parse("http://www.metalrain.ca")));
	}

	@ApiCall
	public ApiResult<?> AddTwo(int a, int b) {
		return new ApiSuccess(a + b);
	}

	@ApiCall
	public ApiResult<Media> Media() {
		return new ApiSuccess<Media>(new Media());
	}

	@ApiCall
	public ApiResult<Website> Website() {
		return new ApiSuccess<Website>(new Website());
	}

	@ApiCall
	public ApiResult createUser(String identifier, String password) {
		try {
			UserService userService = MySaasa.getInstance().getInjector().getProvider(UserService.class).get();
			if (userService.userExists(identifier)) {
				return new ApiError(new IllegalStateException("User already exists"));
			}

			Website current = Website.getCurrent();
			User u = userService.createUser(identifier, password, current.getOrganization());
			SessionService.get().registerUser(Session.get(), u);
			Session.get().bind();
			return new ApiSuccess(new SessionSummary());
		} catch (Exception e) {
			e.printStackTrace();
			return new ApiError<String>(new IllegalStateException("Could not create: " + e.getMessage()));
		}
	}

	@ApiCall
	public ApiResult registerGcmKey(String gc_reg_id) {
		User u = SecurityContext.get().getUser();
		if (u == null)
			return new ApiNotAuthorized();
		UserService.get().RegisterUserGcm(u, gc_reg_id);
		return new ApiSuccess("Received");
	}

	@ApiCall
	public ApiResult logout() {
		SessionService.get().unregisterSession(Session.get());
		return new ApiSuccess(true);
	}

	@ApiCall
	public ApiResult loginUser(String identifier, String password) {
		Session s = Session.get();
		SessionService.get().unregisterSession(s);
		//Look for user
		User u = null;
		try {
			u = UserService.get().findUser(identifier, password);
		} catch (UserDisabledException e) {
			return new ApiError(new IllegalStateException("User is disabled"));
		}

		if (u == null) {
			return new ApiError(new IllegalStateException("Username and/or password was incorrect"));
		} else {
			Session.get().clear();
			SessionService.get().registerUser(Session.get(), u);
			Session.get().bind();

			return new ApiSuccess(new SessionSummary());
		}
	}

	@ApiCall
	public ApiResult getSession() {
		SecurityContext sc = SessionService.get().getSecurityContext(Session.get());
		if (sc == null)
			return new ApiError(new RuntimeException("No Session"));
		return new ApiSuccess(sc);
	}

	/**
	 * Keep-alive
	 * @return "pong"
	 */
	@ApiCall
	public ApiResult ping() {
		return new ApiSuccess("pong");
	}

}
