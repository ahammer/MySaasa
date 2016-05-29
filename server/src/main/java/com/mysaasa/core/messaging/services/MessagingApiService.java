package com.mysaasa.core.messaging.services;

import com.mysaasa.api.ApiError;
import com.mysaasa.api.ApiNotAuthorized;
import com.mysaasa.api.ApiResult;
import com.mysaasa.api.ApiSuccess;
import com.mysaasa.core.security.services.SessionService;
import com.mysaasa.core.security.services.session.SecurityContext;
import com.mysaasa.core.users.model.ContactInfo;
import com.mysaasa.core.users.model.User;
import com.mysaasa.core.users.service.UserService;
import com.mysaasa.interfaces.IApiService;
import com.mysaasa.interfaces.annotations.ApiCall;
import com.mysaasa.interfaces.annotations.SimpleService;
import com.mysaasa.core.messaging.model.Message;
import org.apache.wicket.Session;

/**
 * This service is responsible for basic message stuff
 *
 * Created by Adam on 3/27/2015.
 */
@SimpleService
public class MessagingApiService implements IApiService {
	@ApiCall
	public ApiResult getMessageCount() {
		SecurityContext sc = SessionService.get().getSecurityContext(Session.get());
		if (sc == null)
			return new ApiNotAuthorized();
		return new ApiSuccess(MessagingService.get().getMessageCount());
	}

	@ApiCall
	public ApiResult getMessages(long page, long page_size, String order, String direction) {
		SecurityContext sc = SessionService.get().getSecurityContext(Session.get());
		if (sc == null)
			return new ApiNotAuthorized();
		return new ApiSuccess(MessagingService.get().getMessages(sc.getUser(), page, page_size, order, direction));
	}

	@ApiCall
	public ApiResult getThread(long message_id) {
		try {
			return new ApiSuccess(MessagingService.get().getThread(MessagingService.get().findMessage(message_id)));
		} catch (Exception e) {
			return new ApiError(e);
		}
	}

	@ApiCall
	public ApiResult sendMessage(String to_user, String title, String body, String name, String email, String phone) {
		try {
			User u = UserService.get().getUser(to_user);
			ContactInfo contactInfo = new ContactInfo();
			contactInfo.setEmail(email);
			contactInfo.setHomePhone(phone);
			contactInfo.setName(name);
			Message msg = new Message();
			msg.setRecipient(u);
			msg.setSenderContactInfo(contactInfo);
			msg.setTitle(title);
			msg.setBody(body);
			return new ApiSuccess(MessagingService.get().saveMessage(msg, true));
		} catch (Exception e) {
			return new ApiError(e);
		}
	}

	@ApiCall
	public ApiResult replyMessage(long message_id, String message) {
		try {
			MessagingService service = MessagingService.get();
			return new ApiSuccess(service.replyMessage(service.findMessage(message_id), message));
		} catch (Exception e) {
			e.printStackTrace();
			return new ApiError(e);
		}
	}
}
