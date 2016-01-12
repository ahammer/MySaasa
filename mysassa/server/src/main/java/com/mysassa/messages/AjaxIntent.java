package com.mysassa.messages;

import com.mysassa.messages.data.Bundle;
import com.mysassa.Simple;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.event.IEventSource;

import java.util.logging.Logger;

/**
 * Similar to a Android intent, it's an Action and a Bundle
 *
 * Reason being, it's a generic interface for generic messages, I think
 * I'd like to encourage it's use.
 *
 * Created by administrator on 2014-05-19.
 */
public class AjaxIntent extends AjaxMessage {
	private final AjaxRequestTarget target;
	private final IEventSource source;

	public AjaxIntent(AjaxIntent intent) {
		target = intent.getAjaxRequestTarget();
		extras.addAll(intent.getExtras());
		source = target.getPage();
	}

	public AjaxIntent(AjaxRequestTarget target) {
		this.target = target;
		if (target != null) {
			source = target.getPage();
			return;
		}
		throw new IllegalStateException("We need a source to send this event");
	}

	public AjaxIntent(IEventSource source) {
		this.source = source;
		target = null;
	}

	public String getAction() {
		return action;
	}

	private String action;
	final Bundle extras = new Bundle();

	public Bundle getExtras() {
		return extras;
	}

	@Override
	public AjaxRequestTarget getAjaxRequestTarget() {
		return target;
	}

	public AjaxIntent setAction(String action) {
		this.action = action;
		return this;
	}

	final static Logger logger = Logger.getLogger("AjaxIntent");

	public void send(IEventSource p) {
		logger.info("Sending a intent: " + action + " Extras: " + extras);
		p.send(Simple.get(), Broadcast.BREADTH, this);
	}

	public void send() {
		if (source == null)
			throw new IllegalStateException("Target is null, this won't work");
		send(source);
	}

	@Override
	public String toString() {
		return "AjaxIntent{" + "target=" + target + ", source=" + source + ", action='" + action + '\'' + ", extras=" + extras + '}';
	}
}
