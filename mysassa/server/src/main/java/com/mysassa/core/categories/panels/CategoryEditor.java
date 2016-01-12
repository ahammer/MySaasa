package com.mysassa.core.categories.panels;

import com.mysassa.core.categories.CategoriesModule;
import com.mysassa.core.categories.CategoryService;
import com.mysassa.core.categories.model.Category;
import com.mysassa.messages.MessageHelpers;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.AjaxEditableLabel;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;

/**
 * Created by Adam on 4/14/2015.
 */
public class CategoryEditor extends Panel {
	ModalWindow modal;
	FeedbackPanel feedback;

	public CategoryEditor(String id, CompoundPropertyModel<Category> categoryCompoundPropertyModel) {
		super(id, categoryCompoundPropertyModel);
		add(modal = new ModalWindow("modal"));
		add(feedback = new FeedbackPanel("feedback"));
		add(new Label("type", categoryCompoundPropertyModel.getObject().toFriendlyType()));
		add(new AjaxEditableLabel("name") {
			@Override
			protected void onSubmit(AjaxRequestTarget target) {
				super.onSubmit(target);
				CategoryService.get().saveCategory(categoryCompoundPropertyModel.getObject());
				MessageHelpers.notifyUpdate(target, categoryCompoundPropertyModel.getObject());

			}
		});
		add(new AjaxLink("deleteLink") {
			@Override
			public void onClick(AjaxRequestTarget target) {
				try {
					CategoryService.get().deleteCategory(categoryCompoundPropertyModel.getObject());
					MessageHelpers.gotoModule(target, CategoriesModule.class);
				} catch (Exception e) {
					modal.setTitle("Warning");
					modal.setContent(new Label(modal.getContentId(), "Can only remove unused categories at this time"));
					modal.show(target);
				}
			}
		});

	}
}
