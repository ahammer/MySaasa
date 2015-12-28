package com.mysassa.simple.core.media.panels;

import java.util.ArrayList;

import com.mysassa.simple.messages.ModuleClickedMessage;
import com.mysassa.simple.core.media.messages.MediaUpdatedMessage;
import com.mysassa.simple.core.website.WebsiteModule;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.ISortableDataProvider;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import com.mysassa.simple.core.website.model.Website;

public class MediaDataTable extends AjaxFallbackDefaultDataTable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1620336684893904954L;
	private static final ArrayList<IColumn> columns = new ArrayList<IColumn>();

	static {
		columns.add(new PropertyColumn(new Model<String>("ImplementationScope"), "type"));
		columns.add(new PropertyColumn(new Model<String>("Format"), "format"));
		columns.add(new PropertyColumn(new Model<String>("Filename"), "filename"));
	}

	public MediaDataTable(String id, ISortableDataProvider dataProvider, int rowsPerPage) {
		super(id, columns, dataProvider, rowsPerPage);
	}

	// override this method of the DataTable class
	@Override
	protected Item newRowItem(final String id, final int index, final IModel model) {

		final Item rowItem = super.newRowItem(id, index, model);
		rowItem.add(new AjaxEventBehavior("onclick") {
			private static final long serialVersionUID = 6720512493017210281L;

			@Override
			protected void onEvent(final AjaxRequestTarget target) {
				if (rowItem.getModelObject() instanceof Website) {
					Website website = (Website) rowItem.getModelObject();
					new ModuleClickedMessage(new WebsiteModule(), new Model(website), target).send();
				}
			}

		});
		return rowItem;

	}

	@Override
	public void onEvent(IEvent event) {
		final Object payload = event.getPayload();
		if (payload instanceof MediaUpdatedMessage) {
			final MediaUpdatedMessage msg = (MediaUpdatedMessage) (payload);
			msg.getAjaxRequestTarget().add(this);
		}
	}

}
