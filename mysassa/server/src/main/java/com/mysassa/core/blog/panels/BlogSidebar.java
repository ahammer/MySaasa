package com.mysassa.core.blog.panels;

import com.mysassa.core.blog.model.BlogPost;
import com.mysassa.core.categories.model.Category;
import com.mysassa.core.categories.CategoryService;
import com.mysassa.core.security.services.session.SecurityContext;
import com.mysassa.core.blog.services.BlogService;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class BlogSidebar extends Panel {
	private static final long serialVersionUID = -8265087544848152254L;
	private final BlogDataTable table;

	String selectedCategory;

	public String getSelectedCategory() {
		return selectedCategory;
	}

	public void setSelectedCategory(String selectedCategory) {
		this.selectedCategory = selectedCategory;
	}

	List<Category> selectedCategories;

	public BlogSidebar(String id) {
		super(id);

		selectedCategories = new ArrayList();
		add(table = new BlogDataTable(new Model((Serializable) selectedCategories), new BlogDataProvider()));
		table.setOutputMarkupId(true);
		add(new CategoryForm());

	}

	private class CategoryForm extends Form {
		public CategoryForm() {
			super("categoryForm");
			CategoryChooser chooser;
			final FeedbackPanel feedback;
			add(chooser = new CategoryChooser("blogSidebarCategories", new PropertyModel<Category>(BlogSidebar.this, "selectedCategory")));
			add(feedback = new FeedbackPanel("feedback"));
			chooser.setOutputMarkupId(true);
			feedback.setOutputMarkupId(true);
			chooser.add(new AjaxFormSubmitBehavior(CategoryForm.this, "change") {
				@Override
				protected void onSubmit(AjaxRequestTarget target) {
					target.add(feedback);
					System.out.println(selectedCategory);
					selectedCategories.clear();
					selectedCategories.add(CategoryService.get().findCategory(selectedCategory, BlogPost.class, SecurityContext.get().getUser().getOrganization()));

					target.add(table);
				}

				@Override
				protected void onError(AjaxRequestTarget target) {
					target.add(feedback);
				}
			});
		}

		private class CategoryChooser extends AutoCompleteTextField<String> {
			private CategoryChooser(String id, IModel model) {
				super(id, model);
			}

			@Override
			protected Iterator getChoices(String input) {
				return BlogService.get().getBlogCategories(SecurityContext.get().getUser().getOrganization()).iterator();
			}

		}

	}

	private class BlogDataProvider extends SortableDataProvider {
		private BlogDataProvider() {
			super();
			setSort("priority", SortOrder.DESCENDING);

		}

		@Override
		public Iterator iterator(long first, long count) {
			BlogService blogDataService = BlogService.get();
			return blogDataService.getBlogPostsByCategory(SecurityContext.get().getUser().getOrganization(), selectedCategories, 0, 10, getSort().getProperty().toString(), getSort().isAscending() ? "ASC" : "DESC").subList((int) first, (int) (first + count)).iterator();
		}

		@Override
		public long size() {
			BlogService blogDataService = BlogService.get();
			//Todo this is limited to 10
			return blogDataService.getBlogPostsByCategory(SecurityContext.get().getUser().getOrganization(), selectedCategories, 0, 10, getSort().getProperty().toString(), getSort().isAscending() ? "ASC" : "DESC").size();
		}

		@Override
		public IModel model(Object object) {
			return new CompoundPropertyModel(object);
		}

	}
}
