package com.mysassa.simple.core.blog.panels;

import com.mysassa.simple.core.blog.model.BlogPost;
import com.mysassa.simple.core.website.messages.WebsiteContextChanged;
import com.mysassa.simple.messages.ACTIONS;
import com.mysassa.simple.messages.AjaxIntent;
import com.mysassa.simple.messages.DataUpdateEvent;
import com.mysassa.simple.messages.MessageHelpers;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.ISortableDataProvider;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.ArrayList;

public class BlogDataTable extends AjaxFallbackDefaultDataTable {
	private final static ArrayList<IColumn<String, BlogPost>> COLUMNS = new ArrayList<IColumn<String, BlogPost>>();

	static {
		COLUMNS.add(new AbstractColumn(new Model<String>("Priority"), "priority") {
			@Override
			public void populateItem(Item cellItem, String componentId, IModel rowModel) {
				cellItem.add(new BlogRow(componentId, rowModel));
				cellItem.add(new AttributeModifier("colspan", 3));
			}
		});
		COLUMNS.add(new AbstractColumn(new Model<String>("Title"), "title") {
			@Override
			public void populateItem(Item cellItem, String componentId, IModel rowModel) {
				cellItem.setVisible(false);
				cellItem.add(new Label(componentId, new Model<>("")).setVisible(false));
			}
		});
		COLUMNS.add(new AbstractColumn(new Model<String>("Created"), "dateCreated") {
			@Override
			public void populateItem(Item cellItem, String componentId, IModel rowModel) {
				cellItem.setVisible(false);
				cellItem.add(new Label(componentId, new Model<>("")).setVisible(false));
			}
		});
	}

	private final Model categoryModel;

	public BlogDataTable(Model model, ISortableDataProvider sortableDataProvider) {
		super("BlogDataTable", COLUMNS, sortableDataProvider, 20);
		this.categoryModel = model;

	}

	@Override
	protected org.apache.wicket.markup.repeater.Item newRowItem(java.lang.String id, int index, org.apache.wicket.model.IModel model) {
		final Item item = super.newRowItem(id, index, model);
		item.add(new AjaxEventBehavior("click") {
			@Override
			protected void onEvent(final AjaxRequestTarget target) {
				MessageHelpers.editEventMessage(target, new Model((BlogPost) item.getModelObject()));
			}
		});
		return item;
	}

	@Override
	public void onEvent(IEvent event) {
		if (event.getPayload() instanceof WebsiteContextChanged) {
			AjaxRequestTarget target = ((WebsiteContextChanged) event.getPayload()).getAjaxRequestTarget();
			if (target != null)
				target.add(this);
		} else if (event.getPayload() instanceof DataUpdateEvent) {
			AjaxRequestTarget target = ((DataUpdateEvent) event.getPayload()).getAjaxRequestTarget();
			if (target != null)
				target.add(this);
		} else if (event.getPayload() instanceof AjaxIntent) {
			AjaxIntent wcu = (AjaxIntent) event.getPayload();
			if (wcu.getAction().equals(ACTIONS.ACTION_USER_PREFS_UPDATED)) {
				wcu.getAjaxRequestTarget().add(this);
			}
		}
	}

}
