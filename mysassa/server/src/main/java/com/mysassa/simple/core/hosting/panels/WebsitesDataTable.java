package com.mysassa.simple.core.hosting.panels;

import java.util.ArrayList;

import org.apache.wicket.event.IEvent;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.ISortableDataProvider;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import com.mysassa.simple.core.hosting.messages.WebsiteDataChanged;

public class WebsitesDataTable extends AjaxFallbackDefaultDataTable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1620336684893904954L;
	private static final ArrayList<IColumn> columns = new ArrayList<IColumn>();

	static {
		columns.add(new AbstractColumn(new Model<String>("Website")) {

			@Override
			public void populateItem(Item cellItem, String componentId, IModel rowModel) {
				cellItem.add(new HostingRow(componentId, rowModel));
			}
		});
	}

	public WebsitesDataTable(String id, ISortableDataProvider dataProvider, int rowsPerPage) {
		super(id, columns, dataProvider, rowsPerPage);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onEvent(IEvent event) {
		final Object payload = event.getPayload();
		if (payload instanceof WebsiteDataChanged) {
			final WebsiteDataChanged msg = (WebsiteDataChanged) (payload);
			msg.getAjaxRequestTarget().add(this);
		}
	}

}
