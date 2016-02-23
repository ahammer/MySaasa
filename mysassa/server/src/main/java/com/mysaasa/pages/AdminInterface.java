package com.mysaasa.pages;

import com.mysaasa.ui.content.PanelContainer;
import org.apache.wicket.markup.repeater.RepeatingView;

public interface AdminInterface {
	PanelContainer getMainSection();

	RepeatingView getHeaderButtons();
}
