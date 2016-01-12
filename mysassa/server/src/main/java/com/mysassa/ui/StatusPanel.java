package com.mysassa.ui;

import com.mysassa.messages.AjaxIntent;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnLoadHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;

/**
 * Created by administrator on 2014-05-28.
 */
public class StatusPanel extends Panel {
	private final WebMarkupContainer spinner;
	String label = "Status: OK";

	public StatusPanel(String id) {
		super(id);
		add(new Label("label", new PropertyModel(this, "label")));
		add(spinner = new WebMarkupContainer("spinner"));
		spinner.setOutputMarkupId(true);
		spinner.setOutputMarkupPlaceholderTag(true);
		spinner.setVisible(false);
		setOutputMarkupId(true);
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		response.render(OnLoadHeaderItem.forScript(
				"function SimpleStatusPanelShow(){ $(\"#spinner\").show(); } " + "function SimpleStatusPanelHide(){ $(\"#spinner\").hide(); } " + "Wicket.Event.subscribe('/ajax/call/beforeSend', function(kqEvent, attributes) {" + "   SimpleStatusPanelShow();" + "});" + "Wicket.Event.subscribe('/ajax/call/success', function(kqEvent, attributes) {" + "   SimpleStatusPanelHide();" + "});" + ""));

	}

	@Override
	public void onEvent(IEvent<?> event) {
		super.onEvent(event);
		if (event.getPayload() instanceof AjaxIntent) {
			AjaxIntent intent = (AjaxIntent) event.getPayload();
			setLabel(intent.getAction());
			if (intent.getAjaxRequestTarget() != null) {

				intent.getAjaxRequestTarget().add(this);
			}
		}

	}
}
