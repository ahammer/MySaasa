package com.mysaasa.core.categories;

import com.mysaasa.MySaasa;
import com.mysaasa.core.categories.model.Category;
import com.mysaasa.interfaces.annotations.SimpleService;
import com.mysaasa.core.organization.model.Organization;
import org.apache.commons.collections.ListUtils;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.List;

/**
 * Created by adam on 14-11-08.
 */
@SimpleService
public class CategoryService {
	@Inject
	EntityManager em;
	public static CategoryService get() {
		return MySaasa.getInstance().getInjector().getProvider(CategoryService.class).get();
	}

	/**
	 * Finds a Category object by name.
	 * @param name name
	 * @param o  organization
	 * @param type type
	 * @return the category or null
	 */
	public Category findCategory(String name, Class type, Organization o) {
		if (name == null)
			throw new NullPointerException();
		if (name.trim().equals(""))
			throw new IllegalArgumentException("Blank Category");
		
		List<Category> list;
		if (o != null) {
			list = ListUtils.unmodifiableList(em.createQuery("SELECT C FROM Category C WHERE C.name=:name AND C.organization=:org AND C.type=:cattype ORDER BY C.id DESC")
					.setParameter("name", name)
					.setParameter("org", o)
					.setParameter("cattype", type.getSimpleName())
					.getResultList());
		} else {
			list = ListUtils.unmodifiableList(em.createQuery("SELECT C FROM Category C WHERE C.name=:name AND C.type=:cattype ORDER BY C.id DESC")
					.setParameter("name", name)
					.setParameter("cattype", type.getSimpleName())
					.getResultList());

		}
		if (list.size() == 0) {
			Category category = new Category(type.getSimpleName(), o);
			category.setName(name);
			em.close();
			return saveCategory(category);
		}
		em.detach(list.get(0));//Need to do? Don't do this elsewhere?
		em.close();
		return list.get(0);
	}

	public Category saveCategory(Category blogCategory) {
		
		em.getTransaction().begin();
		Category tracked = em.merge(blogCategory);
		em.flush();
		em.getTransaction().commit();
		em.close();
		return tracked;
	}

	public List<Category> getCategories(Organization organization, Class type) {
		
		if (type != null) {
			return ListUtils.unmodifiableList(em.createQuery("SELECT C FROM Category C WHERE C.organization=:org AND C.type=:cattype ORDER BY C.type")
					.setParameter("org", organization)
					.setParameter("cattype", type.getSimpleName())
					.getResultList());
		} else {
			return ListUtils.unmodifiableList(em.createQuery("SELECT C FROM Category C WHERE C.organization=:org ORDER BY C.type")
					.setParameter("org", organization)
					.getResultList());

		}
	}

	public void deleteCategory(Category cat) {
		em.getTransaction().begin();
		em.remove(em.merge(cat));
		em.flush();
		em.getTransaction().commit();
	}
}
