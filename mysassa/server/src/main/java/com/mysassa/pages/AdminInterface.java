package com.mysassa.pages;

import com.mysassa.ui.content.PanelContainer;
import org.apache.wicket.markup.repeater.RepeatingView;

public interface AdminInterface {
	PanelContainer getMainSection();

	RepeatingView getHeaderButtons();
}
