package com.mysassa.simple.pages;

import com.mysassa.simple.ui.content.PanelContainer;
import org.apache.wicket.markup.repeater.RepeatingView;

public interface AdminInterface {
	PanelContainer getMainSection();

	RepeatingView getHeaderButtons();
}
