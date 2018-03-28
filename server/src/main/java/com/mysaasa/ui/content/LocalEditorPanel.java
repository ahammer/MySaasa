package com.mysaasa.ui.content;

import com.mysaasa.Simple;
import com.mysaasa.interfaces.IClassPanelAdapter;
import com.mysaasa.messages.ACTIONS;
import com.mysaasa.messages.AjaxIntent;
import com.mysaasa.messages.EditContentMessage;
import com.mysaasa.messages.ModuleClickedMessage;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.model.PropertyModel;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The editor-panel, for editing content.
 *
 * Meant for "located" data, e.g. certain ID's. Usually for model editor, but maybe for other uses in some places.
 *
 * Usually the Modules Edit Mappings will load the appropriate view for the appropriate model into here.
 *
 * <p>This responds to ModuleClickedMessage, when it's received it loads the AbstractModules main panel </p>
 */
public class LocalEditorPanel extends PanelContainer {
	private static final long serialVersionUID = 8007394055642359017L;
	private AbstractDefaultAjaxBehavior ajaxInlineEditorCallback;

	//This is the Style we are going to apply to left: in order to push it over
	private String leftStyle = "";

	public String getLeftStyle() {
		return leftStyle;
	}

	public void setLeftStyle(String leftStyle) {
		this.leftStyle = leftStyle;
	}

	public LocalEditorPanel() {
		super("editorPanel");
		add(ajaxInlineEditorCallback = new AbstractDefaultAjaxBehavior() {
			@Override
			protected void respond(AjaxRequestTarget target) {
				AjaxIntent intent = new AjaxIntent(target);
				intent.setAction(ACTIONS.ACTION_REQUEST_SAVE);
				intent.send();
				System.out.println("Clicked back");
			}
		});

		add(new AttributeModifier("style", new PropertyModel(this, "leftStyle")));
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		//Listen for CTRL-S in the Browser
		response.render(JavaScriptHeaderItem.forScript(
				("$(window).keypress(function(event) {\n" + "    if (!(event.which == 115 && event.ctrlKey) && !(event.which == 19)) return true;\n" + "    Wicket.Ajax.post({u:'%callbackUrl%'});\n" + "    event.preventDefault();\n" + "    return false;\n" + "});")
						.replace("%callbackUrl%", ajaxInlineEditorCallback.getCallbackUrl().toString()),
				"ModelEditorPanel"));
	}

	@Override
	public void onEvent(IEvent<?> event) {
		final Object payload = event.getPayload();
		//If through a EditMessage
		if (payload instanceof EditContentMessage) {
			final EditContentMessage msg = (EditContentMessage) (payload);
			Object obj = msg.getModel().getObject();

			/**
			 * TODO we should register Providers with the Module Manager, and then getInstance a panel from the provider
			 *
			 * It'll allow more flexible logic provided by the Module and not by this panel
			 */
			IClassPanelAdapter adapter = Simple.getInstance().getClassPanelAdapter(obj.getClass());
			if (adapter.isFullscreen()) {
				leftStyle = "left:0px;"; //Shove the left over to absolute 0
			} else {
				leftStyle = "";
			}
			checkNotNull(adapter, "Could not find adapter for " + obj.getClass());

			setInnerPanel(adapter.getEditPanel(CONTENT_ID, obj));
			if (msg.getAjaxRequestTarget() != null)
				msg.getAjaxRequestTarget().add(this);
		}

		//When a module loads by clicking, it getInstance's to choose the menu and main section, so it over-rides this
		//Through ModuleClickedMessage
		if (payload instanceof ModuleClickedMessage) {
			ModuleClickedMessage mcm = (ModuleClickedMessage) payload;
			leftStyle = "";
			setInnerPanel(mcm.getModule().getMainPanel(CONTENT_ID, mcm.getModel()));
			if (((ModuleClickedMessage) payload).getAjaxRequestTarget() == null)
				return;
			mcm.getAjaxRequestTarget().add(this);
		}
	}
}
