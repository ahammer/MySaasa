package com.mysaasa.core.blog;

import com.mysaasa.core.blog.model.BlogPost;
import com.mysaasa.core.blog.panels.BlogSidebar;
import com.mysaasa.core.users.model.User;
import com.mysaasa.interfaces.AbstractClassPanelAdapter;
import com.mysaasa.interfaces.IClassPanelAdapter;
import com.mysaasa.core.AbstractModule;
import com.mysaasa.core.blog.panels.BlogPoster;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;

public class BlogModule extends AbstractModule {

	private static final long serialVersionUID = 1L;

	@Override
	public boolean hasAccess(User.AccessLevel UserAccessLevel) {
		switch (UserAccessLevel) {
		case WWW:
		case ORG:
		case ROOT:
			return true;
		default:
			return false;
		}
	}

	@Override
	public Model getDefaultModel() {
		return new Model(new BlogPost());
	}

	@Override
	public Component getMenuDropDownPanel(String id, IModel model) {
		return null;
	}

	@Override
	public Component getSidebarPanel(String id, IModel model) {
		return new BlogSidebar(id);
	}

	@Override
	public Component getMainPanel(String id, IModel model) {
		if (model == null) {
			model = new Model(new BlogPost());
		}
		return new BlogPoster(id, new CompoundPropertyModel<BlogPost>((BlogPost) model.getObject()));
	}

	@Override
	public String getMenuTitle() {
		return "Blog";
	}

	@Override
	public Map<Class, IClassPanelAdapter> getClassPanelAdapters() {
		HashMap<Class, IClassPanelAdapter> result = new HashMap();
		result.put(BlogPost.class, new AbstractClassPanelAdapter<BlogPost>() {
			@Override
			public Panel getEditPanel(String id, BlogPost o) {
				return new BlogPoster(id, new CompoundPropertyModel<BlogPost>(o));
			}
		});
		return result;
	}

}
