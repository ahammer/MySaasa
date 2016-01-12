package com.mysassa.pages.docs.api;

import com.mysassa.api.ApiMapping;
import com.mysassa.api.ApiParameter;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.Collection;

/**
 * Created by administrator on 3/16/2014.
 */
public class Function extends Panel {
	public Function(String id, IModel<ApiMapping> model) {
		super(id, model);
		add(new FunctionForm());
	}

	private class FunctionForm extends WebMarkupContainer {
		public FunctionForm() {
			super("form", Function.this.getDefaultModel());

			ApiMapping mapping = (ApiMapping) Function.this.getDefaultModelObject();
			add(new AttributeModifier("action", new Model(
					"/" + mapping.getMethod().getDeclaringClass().getSimpleName() + "/" + mapping.getMethod().getName())));

			add(new Label("method", new Model(mapping.getMethod().getName())));
			add(new Label("class", new Model(mapping.getMethod().getDeclaringClass().getSimpleName())));
			add(new ListView("params", Model.of((Collection) mapping.getParameters())) {

				@Override
				protected void populateItem(ListItem listItem) {
					ApiParameter p = (ApiParameter) listItem.getModelObject();
					listItem.add(new Label("name", new Model(p.getName())));
					listItem.add(new Label("type", new Model(p.get_class().getSimpleName())));
					WebMarkupContainer field;
					listItem.add(field = new WebMarkupContainer("field"));
					field.add(new AttributeModifier("name", new Model(p.getName())));
				}
			});
			add(new FeedbackPanel("feedback"));

		}

	}
}
