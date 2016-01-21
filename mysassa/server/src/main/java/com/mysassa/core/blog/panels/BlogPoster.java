package com.mysassa.core.blog.panels;

import com.mysassa.SimpleImpl;
import com.mysassa.core.blog.model.BlogPost;
import com.mysassa.core.blog.services.BlogService;
import com.mysassa.core.categories.CategoryService;
import com.mysassa.core.categories.model.Category;
import com.mysassa.core.media.model.Media;
import com.mysassa.core.media.panels.MediaView;
import com.mysassa.core.security.services.session.SecurityContext;
import com.mysassa.messages.MessageHelpers;
import com.mysassa.core.blog.messages.BlogPostModifiedMessage;
import com.mysassa.ui.multi_select.MultiSelectWidget;
import com.mysassa.ui.FeedbackHover;
import com.mysassa.core.media.panels.uploader.Uploader;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

/**
 *  Panel that manages a postToBlog
 * <p>You give it a model of a CompountPropertyModel of a postToBlog, and the fields/content
 * will all be editable/creatable.</p>
 * <p>Automatically manages saving/editing with help of the BlogService.</p>

 */
public class BlogPoster extends Panel {

	private final BlogPosterForm blogPosterForm;

	public BlogPoster(String id, CompoundPropertyModel<BlogPost> model) {
		super(id, model);
		if (model.getObject().getCategories() == null) {
			throw new IllegalArgumentException("Categories is null");
		}
		add(blogPosterForm = new BlogPosterForm(model));

	}

	@Override
	public void renderHead(IHeaderResponse response) {
		final String initBlogPoster = "CKEDITOR.replace('" + blogPosterForm.body.getId() + "');";// +
		//"CKEDITOR.replace('"+blogPosterForm.summary.getId()+"');";
		response.render(OnDomReadyHeaderItem.forScript(initBlogPoster));

	}

	public class BlogPosterForm extends Form {
		private final FormComponent body;
		private final FormComponent summary;

		@Override
		protected void onConfigure() {
			super.onConfigure();
			BlogPost user = (BlogPost) getModelObject();
			editLabel.setVisible(user.getId() != 0);
			newLabel.setVisible(user.getId() == 0);
		}

		private final FeedbackHover feedbackHover;
		private final MultiSelectWidget categoryChooser;
		private final WebMarkupContainer newLabel;
		private final WebMarkupContainer editLabel;

		public BlogPosterForm(final IModel<BlogPost> model) {
			super("BlogForm", model);

			add(new Uploader("uploader") {
				@Override
				public void done(AjaxRequestTarget target, List<Media> uploads) {
					if (getModelObject() instanceof BlogPost) {
						BlogPost blogPost = (BlogPost) getModelObject();
						blogPost.getMedia().addAll(uploads);
					}
					//("Uploaded some files: "+uploads);
				}
			});

			BlogPost blogpost = model.getObject();
			add(editLabel = new WebMarkupContainer("EditLabel"));
			add(newLabel = new WebMarkupContainer("NewLabel"));

			add(new MediaView("media", Model.of(blogpost.getMedia())));
			add(new TextField("title").setRequired(true));
			add(new TextField("subtitle").setRequired(false));
			add(new TextField("priority").setRequired(false));
			add(new CheckBox("visible"));
			add(summary = new TextArea("summary").setRequired(false));
			add(body = new TextArea("body").setRequired(false));
			add(feedbackHover = new FeedbackHover("feedbackHover"));
			add(categoryChooser = new CategoryChooser("categories", model));

			add(new MySaveButton());
			add(new MyDeleteButton());
		}

		public FeedbackHover getFeedbackHover() {
			return feedbackHover;
		}

		private class MySaveButton extends AjaxButton {
			public MySaveButton() {
				super("saveButton");
			}

			@Override
			public void onSubmit(final AjaxRequestTarget target, Form form) {
				BlogService blogDataService = BlogService.get();
				BlogPost blogpost = (BlogPost) BlogPosterForm.this.getModelObject();
				blogpost.setOrganization(SecurityContext.get().getUser().getOrganization());
				BlogPost saved = blogDataService.saveBlogPost(blogpost);
				MessageHelpers.notifyUpdate(target, saved);
			}

			public void onError(AjaxRequestTarget target, Form form) {
				getFeedbackHover().show(target);
			}
		}

		private class MyDeleteButton extends AjaxButton {
			public MyDeleteButton() {
				super("deleteButton");
			}

			@Override
			protected void onConfigure() {
				super.onConfigure();
				setVisible(((BlogPost) BlogPoster.this.getDefaultModelObject()).id != 0);
			}

			@Override
			public void onSubmit(final AjaxRequestTarget target, Form form) {
				BlogService blogDataService = BlogService.get();
				blogDataService.deleteBlogPost((BlogPost) BlogPosterForm.this.getModelObject());
				getPage().send(SimpleImpl.get(), Broadcast.BREADTH, new BlogPostModifiedMessage() {
					@Override
					public AjaxRequestTarget getAjaxRequestTarget() {
						return target;
					}
				});
				BlogPost bp;
				MessageHelpers.editEventMessage(target, new Model(bp = new BlogPost()));
				MessageHelpers.notifyUpdate(target, bp);
			}

			public void onError(AjaxRequestTarget target, Form form) {
				getFeedbackHover().show(target);
			}
		}

		private class CategoryChooser extends MultiSelectWidget<Category> {
			public CategoryChooser(String id, IModel<BlogPost> model) {
				super(id, new Model((Serializable) model.getObject().getCategories()), new Model("Category"), new Model("Add"));
			}

			@Override
			public Iterator<Category> getChoices() {
				return CategoryService.get().getCategories(SecurityContext.get().getUser().getOrganization(), BlogPost.class).iterator();
			}

			@Override
			public Category fromString(String string) {
				if (string.equals(""))
					throw new IllegalArgumentException("String can't be empty");
				return Category.fromString(string, BlogPost.class, SecurityContext.get().getUser().getOrganization());
			}

			@Override
			public void populateListItem(final ListItem<Category> item, final boolean selected) {
				class MyWebMarkupContainer extends WebMarkupContainer {
					public MyWebMarkupContainer() {

						super("wrapper");
						if (selected) {
							add(new Label("name", new Model(item.getModelObject().getName())));
							AjaxCheckBox checkbox;
							add(checkbox = new AjaxCheckBox("checkbox", new Model(selected)) {
								@Override
								protected void onUpdate(AjaxRequestTarget target) {
									Category bc = item.getModelObject();
									Boolean selected = getModelObject();
									BlogPost currentPost = (BlogPost) BlogPosterForm.this.getModelObject();
									if (selected) {
										currentPost.addCategory(bc.getName());
									} else {
										currentPost.removeCategory(bc);
									}
								}
							});
						} else {
							add(new Label("name", "").setVisible(false));
							add(new Label("checkbox", "").setVisible(false));

						}

					}

				}
				item.add(new MyWebMarkupContainer());

			}

			@Override
			protected String convertToString(Category object) {
				return object.getName();
			}
		}
	}

}
