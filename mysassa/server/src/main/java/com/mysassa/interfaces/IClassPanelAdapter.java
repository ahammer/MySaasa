package com.mysassa.interfaces;

import org.apache.wicket.Component;

/**
 * Created by adam on 2014-10-17.
 */
public interface IClassPanelAdapter<T> {
	public Component getEditPanel(String id, T o);

	public boolean isFullscreen();
}
