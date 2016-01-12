package com.mysassa.core.blog.panels;

import com.mysassa.core.blog.model.BlogPost;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

/**
 * Created by adam on 15-01-25.
 */
public class BlogRow extends Panel {
	public BlogRow(String id, IModel<BlogPost> model) {
		super(id, model);
		add(new Label("title"));
		add(new Label("subtitle"));
		add(new Label("summary"));
		add(new Label("priority"));
	}
}
