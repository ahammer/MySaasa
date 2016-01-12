package com.mysassa.ui.menu;

import com.mysassa.messages.ModuleClickedMessage;
import com.mysassa.ui.content.PanelContainer;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import com.mysassa.core.AbstractModule;

//Todo, should use a model
/* AbstractModuleLink
*
* This is a link to a AbstractModule,
* it has a label, and sends a message when clicked*/
public abstract class AbstractModuleLink extends Panel {
	private static final long serialVersionUID = 1L;
	private final LocalAjaxLink link;

	public AbstractModuleLink(String id, Model<AbstractModule> model) {
		super(id, model);

		add(link = new LocalAjaxLink());
		setOutputMarkupId(true);
	}

	public Component getLink() {
		return link.title;
	}

	private class LocalAjaxLink extends WebMarkupContainer {
		private static final long serialVersionUID = 1L;
		private final PanelContainer container;
		AjaxLink title;

		public LocalAjaxLink() {
			super("link");

			add(title = new AjaxLink("title", new PropertyModel<String>(AbstractModuleLink.this.getDefaultModelObject(), "title")) {
				@Override
				public void onClick(final AjaxRequestTarget target) {
					new ModuleClickedMessage((AbstractModule) AbstractModuleLink.this.getDefaultModelObject(), target).send();
					clicked();
				}
			});
			AbstractModule module = (AbstractModule) AbstractModuleLink.this.getDefaultModelObject();
			title.add(new Label("label", Model.of(module.getMenuTitle())));
			add(container = new PanelContainer("options"));
			Component m = module.getMenuDropDownPanel(PanelContainer.CONTENT_ID, module.getDefaultModel());
			if (m != null) {
				container.setInnerPanel(m);
			} else {
				container.setVisible(false);
			}

		}

	}

	@Override
	public void onEvent(IEvent<?> event) {
		super.onEvent(event);
		if (event.getPayload() instanceof ModuleClickedMessage) {

			AjaxRequestTarget target = ((ModuleClickedMessage) event.getPayload()).getAjaxRequestTarget();
			if (target != null)
				target.add(this);
		}
	}

	public abstract void clicked();
}
