package com.mysaasa.core.media.panels;

import com.mysaasa.core.media.messages.MediaUpdatedMessage;
import com.mysaasa.core.media.model.Media;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.DynamicImageResource;

import java.util.Collection;

public class MediaView extends Panel {
	private final WebMarkupContainer empty;

	@Override
	protected void onConfigure() {
		super.onConfigure();
		empty.setVisible((((Collection) getDefaultModelObject()).size() == 0));
	}

	public MediaView(String id, IModel<Collection<Media>> model) {
		super(id, model);
		setOutputMarkupId(true);

		add(empty = new WebMarkupContainer("empty"));
		add(new PropertyListView("list", model) {
			@Override
			protected void populateItem(final ListItem listItem) {
				Media media = ((Media) listItem.getDefaultModelObject());
				listItem.add(new Label("filename"));
				listItem.add(new ExternalLink("uid", new Model("/media/" + media.getUid()), new Model("Link")));
				listItem.add(new Image("thumbnail", new AbstractReadOnlyModel<DynamicImageResource>() {
					@Override
					public DynamicImageResource getObject() {
						Media m = (Media) listItem.getModelObject();
						return m.calculateImageData(Media.Size.THUMBNAIL);
					}
				}));
				listItem.add(new AjaxLink("delete") {
					@Override
					public void onClick(AjaxRequestTarget target) {
						Media m = (Media) listItem.getModelObject();
						Collection<Media> c = (Collection<Media>) MediaView.this.getDefaultModelObject();
						c.remove(m);
						target.add(MediaView.this);
					}
				});
			}
		});

	}

	@Override
	public void onEvent(IEvent event) {
		if (event.getPayload() instanceof MediaUpdatedMessage) {
			MediaUpdatedMessage mum = (MediaUpdatedMessage) event.getPayload();
			mum.getAjaxRequestTarget().add(this);
		}
	}

}
