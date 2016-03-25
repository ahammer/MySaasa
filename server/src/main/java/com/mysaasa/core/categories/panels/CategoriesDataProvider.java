package com.mysaasa.core.categories.panels;

import com.mysaasa.Simple;
import com.mysaasa.core.categories.CategoryService;
import com.mysaasa.core.categories.model.Category;
import com.mysaasa.core.security.services.session.SecurityContext;
import com.mysaasa.core.users.model.User;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Iterator;

public class CategoriesDataProvider extends SortableDataProvider<Category, String> {
	private static final long serialVersionUID = 1L;
	private final Class type;

	public CategoriesDataProvider(Class type) {
		this.type = type;
	}

	@Override
	public Iterator<? extends Category> iterator(long first, long count) {
		new ArrayList<User>();
		EntityManager em = Simple.getEm();
		final Iterator<? extends Category> result = CategoryService.get().getCategories(SecurityContext.get().getUser().getOrganization(), type).subList((int) first, (int) (first + count)).iterator();
		em.close();
		return result;
	}

	@Override
	public IModel<Category> model(Category object) {
		return new CompoundPropertyModel(object);
	}

	@Override
	public long size() {
		return CategoryService.get().getCategories(SecurityContext.get().getUser().getOrganization(), type).size();
	}
}
