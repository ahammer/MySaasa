package com.mysaasa.pages;

import com.mysaasa.core.website.model.Website;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;

import java.util.List;

class WebsiteChoiceRenderer implements IChoiceRenderer<Website> {

	@Override
	public Object getDisplayValue(Website object) {
		return object.production;
	}

	@Override
	public String getIdValue(Website object, int index) {
		return String.valueOf(object.getId());
	}

	@Override
	public Website getObject(String id, IModel<? extends List<? extends Website>> choices) {
		for (Website obj : choices.getObject()) {
			if (String.valueOf(obj.getId()).equalsIgnoreCase(id))
				return obj;
		}
		return null;
	}
}
