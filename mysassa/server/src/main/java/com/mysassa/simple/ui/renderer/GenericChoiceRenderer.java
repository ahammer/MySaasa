package com.mysassa.simple.ui.renderer;

import org.apache.wicket.markup.html.form.ChoiceRenderer;

/*
 * This class just uses the string RunContext of the rows model object to display a choice for a drop down menu. Basically a array of string.
 * 
 * This is good for enums->dropdowns or string arrays->drop downs.  SimpleImpl multi-selects
 */
@SuppressWarnings("rawtypes")
public class GenericChoiceRenderer extends ChoiceRenderer {
	/**
	 *
	 */
	private static final long serialVersionUID = 3027311533806232372L;

	@Override
	public Object getDisplayValue(Object objDispl) {
		return objDispl.toString();
	}

	@Override
	public String getIdValue(Object obj, int index) {
		return obj.toString();
	}
}
