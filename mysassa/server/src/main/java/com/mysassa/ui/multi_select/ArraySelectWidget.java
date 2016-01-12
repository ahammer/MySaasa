package com.mysassa.ui.multi_select;

import org.apache.wicket.model.IModel;

import java.util.List;

/**
 * Created by Adam on 10/11/2014.
 */
public abstract class ArraySelectWidget<T> extends MultiSelectWidget<T> {
	/**
	 * Creates a new MultiSelectWidget, provide it a list of selected items of type T
	 *
	 * @param id
	 * @param model
	 */
	protected ArraySelectWidget(String id, IModel<List<T>> model, IModel<String> placeholderModel, IModel<String> buttonModel) {
		super(id, model, placeholderModel, buttonModel);
	}
}
