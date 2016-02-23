package com.mysaasa.pages.tests;

import com.mysaasa.core.blog.model.BlogPost;
import com.mysaasa.core.organization.model.Organization;
import com.mysaasa.core.categories.model.Category;
import com.mysaasa.core.users.model.User;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import java.io.Serializable;

/**
 * Test's that a blog and category Links correctly
 *
 * Created by Adam on 2/13/14.
 */
public class TestBlogLinking extends WebPage implements Serializable {
	public class FormData implements Serializable {
		final User u = new User("", "", User.AccessLevel.GUEST);

		Organization organization = new Organization("test");
		BlogPost blogPost = new BlogPost(u, organization);

		public FormData() {
			blogPost.setTitle("This is a test blogpost from the test page to figure out one to many JPA relationships on the category." + "If you run this, it will post to the blog");
			u.setIdentifier("test@test.ca");
			Category c1 = new Category("test", organization);
			Category c2 = new Category("test", organization);
			c1.setName("Category 1");
			c2.setName("Category 2");
			blogPost.addCategory("New Test 1");
			blogPost.addCategory("New Test 2");
		}

	}

	private final FormData data = new FormData();

	public TestBlogLinking() {
		super();
		add(new MyForm(new CompoundPropertyModel(data)));
	}

	private class MyForm extends Form {
		private final Label label;

		public MyForm(IModel<FormData> model) {
			super("form", model);
			add(label = new Label("label"));
			label.setOutputMarkupId(true);
			add(new AjaxButton("button") {
				@Override
				protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
					super.onSubmit(target, form);
					data.blogPost = data.blogPost.save();

					target.add(label);
					//("PressedButton");
				}
			});

		}
	}

}
