package com.mysaasa.core.categories;

import com.mysaasa.core.categories.model.Category;
import com.mysaasa.core.categories.panels.CategorySidebar;
import com.mysaasa.core.users.model.User;
import com.mysaasa.interfaces.AbstractClassPanelAdapter;
import com.mysaasa.interfaces.IClassPanelAdapter;
import com.mysaasa.core.AbstractModule;
import com.mysaasa.core.categories.panels.CategoryEditor;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Adam on 4/14/2015.
 */
public class CategoriesModule extends AbstractModule {
	@Override
	public String getMenuTitle() {
		return "Categories";
	}

	@Override
	public Component getMenuDropDownPanel(String id, IModel model) {
		return null;
	}

	@Override
	public Component getSidebarPanel(String id, IModel model) {
		return new CategorySidebar(id);
	}

	@Override
	public Component getMainPanel(String id, IModel model) {
		return new Label(id, "Label");
	}

	@Override
	public boolean hasAccess(User.AccessLevel UserAccessLevel) {
		return UserAccessLevel == User.AccessLevel.ORG || User.AccessLevel.ROOT == UserAccessLevel;
	}

	@Override
	public Model getDefaultModel() {
		return new Model();
	}

	@Override
	public Map<Class, IClassPanelAdapter> getClassPanelAdapters() {
		HashMap<Class, IClassPanelAdapter> result = new HashMap();
		result.put(Category.class, new AbstractClassPanelAdapter<Category>() {
			@Override
			public Panel getEditPanel(String id, Category o) {
				return new CategoryEditor(id, new CompoundPropertyModel<Category>(o));
			}
		});
		return result;
	}
}
