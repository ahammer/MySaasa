package com.mysassa.simple.messages;

import org.apache.wicket.ajax.AjaxRequestTarget;

import java.io.Serializable;

public abstract class AjaxMessage implements Serializable {

	protected AjaxMessage() {}

	public abstract AjaxRequestTarget getAjaxRequestTarget();
}
