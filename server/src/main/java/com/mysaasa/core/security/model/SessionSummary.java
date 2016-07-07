package com.mysaasa.core.security.model;

import com.google.gson.annotations.Expose;
import com.mysaasa.core.security.services.session.SecurityContext;
import com.mysaasa.core.users.model.User;
import org.apache.wicket.Session;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.WebRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;

/**
 * Some information about the session for the client
 * So they can make more informed decisions about whether to login or not
 */
public class SessionSummary {
	@Expose
	final int lengthSeconds;

	@Expose
	final Date timestamp = new Date();

	@Expose
	SecurityContext context;

	public SessionSummary() {
		context = SecurityContext.get();
		Request request = RequestCycle.get().getRequest();
		if (request instanceof WebRequest) {
			ServletWebRequest wr = (ServletWebRequest) request;
			HttpSession session = wr.getContainerRequest().getSession();
			this.lengthSeconds = session.getMaxInactiveInterval();
		} else {
			this.lengthSeconds = 0; //Unknown
		}
	}

	public int getLengthSeconds() {
		return lengthSeconds;
	}

	public User getUser() {
		return context.getUser();
	}

	public Date getTimestamp() {
		return timestamp;
	}
}
