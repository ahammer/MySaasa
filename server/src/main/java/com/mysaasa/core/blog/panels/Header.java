package com.mysaasa.core.blog.panels;

import com.mysaasa.core.blog.model.BlogPost;
import com.mysaasa.messages.MessageHelpers;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

public class Header extends Panel {

	private static final long serialVersionUID = 2047188623448222572L;

	public Header(String id) {
		super(id);
		add(new NewPostForm());
	}

	public class NewPostForm extends Form {
		public NewPostForm() {
			super("NewPostForm");
			add(new AjaxLink("NewPost") {
				@Override
				public void onClick(final AjaxRequestTarget target) {
					MessageHelpers.editEventMessage(target, new Model(new BlogPost()));
				}
			});

		}

	}

}
