package com.mysaasa.core.messaging.services;

import com.mysaasa.api.model.ApiSuccess;
import com.mysaasa.core.users.model.User;
import com.mysaasa.core.users.service.UserService;
import com.mysaasa.interfaces.ITemplateService;
import com.mysaasa.interfaces.annotations.SimpleService;
import com.mysaasa.api.model.ApiError;
import com.mysaasa.api.model.ApiResult;
import com.mysaasa.core.messaging.model.Message;
import com.mysaasa.core.users.model.ContactInfo;

/**
 * Created by Adam on 3/27/2015.
 */
@SimpleService
public class MessagingTemplateService implements ITemplateService {
	@Override
	public String getTemplateInterfaceName() {
		return "Messaging";
	}

	/**
	 * Send ths message
	 * @param to_user
	
	 * @param title
	 * @param body
	 * @param email
	 * @return
	 */
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
}
