package com.mysassa.simple.interfaces;

/**
 * Created by adam on 14-11-26.
 */
public abstract class AbstractClassPanelAdapter<T> implements IClassPanelAdapter<T> {
	@Override
	public boolean isFullscreen() {
		return false;
	}
}
