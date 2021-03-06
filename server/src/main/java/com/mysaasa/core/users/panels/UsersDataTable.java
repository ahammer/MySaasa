package com.mysaasa.core.users.panels;

import java.util.ArrayList;

import com.mysaasa.messages.DataUpdateEvent;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.ISortableDataProvider;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import com.mysaasa.core.users.model.User;

public abstract class UsersDataTable extends AjaxFallbackDefaultDataTable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7235830191682657603L;
	private static final ArrayList<IColumn> columns = new ArrayList<IColumn>();

	static {
		columns.add(new AbstractColumn(new Model<String>("User")) {
			@Override
			public void populateItem(Item cellItem, String componentId, IModel rowModel) {
				cellItem.add(new UserRow(componentId, rowModel));
			}
		});
	}

	public UsersDataTable(String id, ISortableDataProvider dataProvider) {
		super(id, columns, dataProvider, 20);
		setOutputMarkupId(true);
	}

	// override this method of the DataTable class

	@SuppressWarnings("unchecked")
	@Override
	protected Item newRowItem(final String id, final int index, final IModel model) {

		final Item rowItem = super.newRowItem(id, index, model);
		rowItem.add(new AjaxEventBehavior("onclick") {

			private static final long serialVersionUID = 6720512493017210281L;

			@Override
			protected void onEvent(AjaxRequestTarget target) {
				if (rowItem.getModelObject() instanceof User) {
					final User u = (User) rowItem.getModelObject();
					onRowClick(target, u);
				}
			}

		});
		return rowItem;

	}

	// Messages Responded to
	// --OrganizationDataChanged
	@Override
	public void onEvent(IEvent event) {
		final Object payload = event.getPayload();
		if (payload instanceof DataUpdateEvent) {
			final DataUpdateEvent msg = (DataUpdateEvent) (payload);
			if (msg.obj instanceof User)
				msg.getAjaxRequestTarget().add(this);
		}
	}

	public abstract void onRowClick(AjaxRequestTarget target, User u);

}
