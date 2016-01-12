package com.mysassa.core.categories.panels;

import com.mysassa.core.categories.model.Category;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

/**
 * Created by Adam on 4/14/2015.
 */
public class CategoryRow extends Panel {
	public CategoryRow(String componentId, IModel rowModel) {
		super(componentId, rowModel);
		add(new Label("category", ((Category) rowModel.getObject()).toFriendlyString()));
	}
}
