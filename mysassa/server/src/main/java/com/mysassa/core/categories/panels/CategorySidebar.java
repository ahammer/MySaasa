package com.mysassa.core.categories.panels;

import com.mysassa.core.categories.model.Category;
import com.mysassa.messages.MessageHelpers;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

public class CategorySidebar extends Panel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6881467492971355182L;

	public CategorySidebar(String id) {
		super(id);
		CategoriesDataProvider provider;
		CategoriesDataTable table;
		add(table = new CategoriesDataTable("CategoriesDataTable", provider = new CategoriesDataProvider()) {
			@Override
			public void onRowClick(AjaxRequestTarget target, Category u) {
				MessageHelpers.editEventMessage(target, new Model(u));
			}
		});
	}

}
