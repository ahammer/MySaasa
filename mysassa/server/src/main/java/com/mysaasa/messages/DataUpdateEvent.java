package com.mysaasa.messages;

import org.apache.wicket.ajax.AjaxRequestTarget;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Data has been updated, moving to a generic event
 * Created by adam on 2014-10-17.
 */
public class DataUpdateEvent extends AjaxIntent {
	public final Object obj;

	public DataUpdateEvent(AjaxRequestTarget target, Object obj) {
		super(target);
		checkNotNull(obj);
		setAction(ACTIONS.ACTION_DATA_UPDATED + "/" + obj.getClass().getSimpleName());
		this.obj = obj;
	}
}
