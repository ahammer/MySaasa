package com.mysaasa.ui.content;

import org.apache.wicket.event.IEvent;
import com.mysaasa.messages.ModuleClickedMessage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;

/**
 * Regional Container
 * Global/Regional/Local for App/Section/Item
 *
 * I like the verbiage, it's less confusing then what I had.
 */
public class RegionalContainer extends PanelContainer {
	private static final long serialVersionUID = 8007394055642359017L;

	public RegionalContainer() {
		super("searchSidebar");
	}

	@Override
	public void onEvent(IEvent<?> event) {
		final Object payload = event.getPayload();
		if (payload instanceof ModuleClickedMessage) {
			final ModuleClickedMessage msg = (ModuleClickedMessage) (payload);

			try {
				setInnerPanel(msg.getModule().getSidebarPanel(CONTENT_ID, msg.getModel()));
			} catch (Exception e) {
				setInnerPanel(new Label(CONTENT_ID, new Model(e.getLocalizedMessage())));
			}

			if (msg.getAjaxRequestTarget() != null) {
				msg.getAjaxRequestTarget().add(this);
			}
		}
	}
}
