package com.mysassa.core.messaging.services;

import com.mysassa.api.ApiSuccess;
import com.mysassa.core.users.model.User;
import com.mysassa.core.users.service.UserService;
import com.mysassa.interfaces.ITemplateService;
import com.mysassa.interfaces.annotations.SimpleService;
import com.mysassa.api.ApiError;
import com.mysassa.api.ApiResult;
import com.mysassa.core.messaging.model.Message;
import com.mysassa.core.users.model.ContactInfo;

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
