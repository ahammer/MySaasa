package com.mysassa.simple.core.users.service;

import com.mysassa.simple.Simple;
import com.mysassa.simple.SimpleImpl;
import com.mysassa.simple.api.ApiNotAuthorized;
import com.mysassa.simple.core.hosting.service.HostingService;
import com.mysassa.simple.core.security.services.SessionService;
import com.mysassa.simple.core.security.services.session.SecurityContext;
import com.mysassa.simple.interfaces.annotations.ApiCall;
import com.mysassa.simple.interfaces.annotations.SimpleService;
import com.mysassa.simple.api.ApiError;
import com.mysassa.simple.api.ApiResult;
import com.mysassa.simple.api.ApiSuccess;
import com.mysassa.simple.interfaces.IApiService;
import com.mysassa.simple.core.website.model.Website;
import com.mysassa.simple.core.media.model.Media;
import com.mysassa.simple.core.users.model.User;
import org.apache.wicket.Session;
import org.apache.wicket.request.Url;

/**
 * Created by Adam on 3/12/14.
 */
@SimpleService
public class UserApiService implements IApiService {
	public static UserApiService get() {
		return Simple.get().getInjector().getProvider(UserApiService.class).get();
	}

	public UserApiService() {}

	@ApiCall
	public ApiResult<String> BasicTest() {
		return new ApiSuccess<String>("You just called Create User");
	}

	@ApiCall
	public ApiResult<Website> WebsiteTest() {
		return new ApiSuccess<Website>(SimpleImpl.get().getInjector().getProvider(

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
			UserService userService = SimpleImpl.get().getInjector().getProvider(UserService.class).get();
			if (userService.userExists(identifier)) {
				return new ApiError("User already exists");
			}

			User u = userService.createUser(identifier, password, Website.getCurrent().getOrganization());
			return new ApiSuccess(u);
		} catch (Exception e) {
			e.printStackTrace();
			return new ApiError<String>("Could not create: " + e.getMessage());
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
		User u = UserService.get().findUser(identifier, password);
		//Todo Move sign in to security Service
		if (u == null) {
			return new ApiError("Username and/or password was incorrect");
		} else {
			SessionService.get().registerUser(Session.get(), u);
			Session.get().bind();
			return new ApiSuccess(u);
		}
	}

	@ApiCall
	public ApiResult getSession() {
		SecurityContext sc = SessionService.get().getSecurityContext(Session.get());
		if (sc == null)
			return new ApiError(false);
		return new ApiSuccess(sc);
	}

	/**
	 * Keep-alive
	 * @return
	 */
	@ApiCall
	public ApiResult ping() {
		return new ApiSuccess("pong");
	}

}
