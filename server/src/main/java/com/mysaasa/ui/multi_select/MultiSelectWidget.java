package com.mysaasa.ui.multi_select;

import org.apache.commons.collections.IteratorUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.DefaultCssAutoCompleteTextField;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListItemModel;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.util.ListModel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
This is abstract class so that a AutoCompleteText box can be Paired with a ListView of some sort
 in order to manage the list etc.
 */
public abstract class MultiSelectWidget<T> extends Panel {
	private final SelectedChoicesListView selectedChoicesView;
	protected final IModel<String> placeholderModel;
	protected final IModel<String> buttonModel;
	protected final NewChoiceAutoCompleteForm autochoice;

	/**
	 * Creates a new MultiSelectWidget, provide it a list of selected items of type T
	
	 * @param id
	 * @param model
	 */
	public MultiSelectWidget(String id, IModel<List<T>> model, IModel<String> placeholderModel, IModel<String> buttonModel) {

		super(id, model);
		this.placeholderModel = placeholderModel;
		this.buttonModel = buttonModel;

		// Validation
		if (getDefaultModelObject() == null) {
			throw new NullPointerException("model can not have null object");
		}
		if (!(getDefaultModelObject() instanceof List)) {
			throw (new IllegalArgumentException("Not of type list " + getDefaultModelObject()));
		}

		//Setup
		setOutputMarkupId(true);
		Iterator<T> choices = getChoices();
		List<T> choices_list = IteratorUtils.toList(choices);

		add(selectedChoicesView = new SelectedChoicesListView(choices_list)).setOutputMarkupId(true);
		add(autochoice = new NewChoiceAutoCompleteForm());
		selectedChoicesView.setOutputMarkupId(true);
	}

	/**
	 * Refreshes a list
	 *
	 * Should be done with model but it's a little funny
	 * @param target
	 */
	public void refresh(AjaxRequestTarget target) {
		List<T> list = (List<T>) MultiSelectWidget.this.getDefaultModel().getObject();
		Iterator<T> choices = getChoices();
		List<T> choices_list = IteratorUtils.toList(choices);
		selectedChoicesView.setModelObject(choices_list);
		target.add(this);
	}

	/**
	 * This class is responsible for the form and validation
	 */
	public class NewChoiceAutoCompleteForm extends Form {
		private FeedbackPanel feedback;
		private String category = null;

		public String getCategory() {
			return category;
		}

		public void setCategory(String category) {
			this.category = category;
		}

		public NewChoiceAutoCompleteForm() {
			super("categoryForm");
			add(new NewChoiceAutoComplete(new PropertyModel(this, "category")));
			add(new AddSelection(this));
			add(feedback = new FeedbackPanel("feedback"));
			feedback.setOutputMarkupId(true);
		}

		private class AddSelection extends AjaxButton {
			public AddSelection(Form<?> form) {
				super("add", form);
				add(new AttributeModifier("value", buttonModel));
			}

			@Override
			public void onSubmit(AjaxRequestTarget target, Form f) {
				String category1 = getCategory();
				if (category1 == null)
					return;
				if (category1.equals("")) {
					return;
				} else {
					List<T> list = (List<T>) MultiSelectWidget.this.getDefaultModel().getObject();
					list.add(fromString(category1));

					Iterator<T> choices = getChoices();
					List<T> choices_list = IteratorUtils.toList(choices);
					selectedChoicesView.setModelObject(choices_list);
					target.add(this);
				}
				target.add(MultiSelectWidget.this);
				target.add(feedback);
			}
		}
	}

	/**
	 * This class is responsible for the Selected list
	 */
	public class SelectedChoicesListView extends ListView<T> {
		public SelectedChoicesListView(List<? extends T> list) {
			super("selected",  new ListModel(list));
		}

		@Override
		protected void populateItem(final ListItem<T> item) {
			List<T> list = (List<T>) MultiSelectWidget.this.getDefaultModel().getObject();
			populateListItem(item, (list.contains(item.getModelObject())));
		}
	}

	/**
	 * The top part of this is a auto-complete text field for creating new objects or getting old ones
	 *
	 */
	private class NewChoiceAutoComplete extends DefaultCssAutoCompleteTextField<String> {
		public NewChoiceAutoComplete(IModel model) {

			super("category", model);
			add(new AttributeModifier("placeholder", placeholderModel));
		}

		@Override
		protected Iterator<String> getChoices(String s) {
			ArrayList<String> list = new ArrayList<String>();
			Iterator<T> itr = MultiSelectWidget.this.getChoices();
			while (itr.hasNext()) {
				list.add(convertToString(itr.next()));
			}
			return list.iterator();
		}
	}

	/**
	 * Get's a Iterator for all choices in Type T.
	 *
	 * Implement before using.
	 *
	 * @return
	 */
	public abstract Iterator<T> getChoices();

	/**
	 * Convert a String into Type T.
	 *
	 * @param string
	 * @return
	 */
	public abstract T fromString(String string);

	/**
	 * Convert a object into a String
	 *
	 * @param object
	 * @return
	 */
	protected abstract String convertToString(T object);

	/**
	 * Each list item needs to be created by the implementing class
	 *
	 * @param item
	 * @param selected
	 */
	public abstract void populateListItem(final ListItem<T> item, boolean selected);

}
