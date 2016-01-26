package com.mysassa.core.categories;

import com.mysassa.core.categories.model.Category;
import com.mysassa.interfaces.annotations.SimpleService;
import com.mysassa.Simple;
import com.mysassa.core.organization.model.Organization;
import org.apache.commons.collections.ListUtils;

import javax.persistence.EntityManager;
import java.util.List;

/**
 * Created by adam on 14-11-08.
 */
@SimpleService
public class CategoryService {
	private List list;

	public static CategoryService get() {
		return Simple.get().getInjector().getProvider(CategoryService.class).get();
	}

	public CategoryService() {}

	/**
	 * Finds a Category object by name.
	 * @param name
	 * @return
	 */
	public Category findCategory(String name, Class type, Organization o) {
		if (name == null)
			throw new NullPointerException();
		if (name.trim().equals(""))
			throw new IllegalArgumentException("Blank Category");
		EntityManager em = Simple.getEm();
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
		EntityManager em = Simple.getEm();
		em.getTransaction().begin();
		Category tracked = em.merge(blogCategory);
		em.flush();
		em.getTransaction().commit();
		em.close();
		return tracked;
	}

	public List<Category> getCategories(Organization organization, Class type) {
		EntityManager em = Simple.getEm();
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
		EntityManager em = Simple.getEm();
		em.getTransaction().begin();
		em.remove(em.merge(cat));
		em.flush();
		em.getTransaction().commit();
	}
}
