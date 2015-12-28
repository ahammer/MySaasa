package com.mysassa.simple.core.organization.services;

import com.mysassa.simple.Simple;
import com.mysassa.simple.core.blog.model.BlogPost;
import com.mysassa.simple.core.blog.services.BlogService;
import com.mysassa.simple.core.categories.CategoryService;
import com.mysassa.simple.core.categories.model.Category;
import com.mysassa.simple.core.hosting.service.HostingService;
import com.mysassa.simple.core.users.model.User;
import com.mysassa.simple.core.users.service.UserService;
import com.mysassa.simple.core.website.model.ContentBinding;
import com.mysassa.simple.core.website.model.Website;
import com.mysassa.simple.core.website.services.WebsiteService;
import com.mysassa.simple.interfaces.annotations.SimpleService;
import com.mysassa.simple.core.organization.model.Organization;
import org.slf4j.Logger;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

@SimpleService
public class OrganizationService {

	public OrganizationService() {
		super();
	}

	/**
	 * TODO Maybe inline some of this and make it one big transaction
	 *
	 * @param organization
	 * @throws IllegalStateException
	 */
	public void deleteOrganization(Organization organization) throws IllegalStateException {
		EntityManager em = Simple.getEm();
		checkNotNull(organization);

		if (organization.getId() == 0) {
			em.close();
			throw new IllegalStateException("Trying to delete a non-existent organization");
		}

		List<BlogPost> posts = BlogService.get().getBlogPosts(organization);
		for (BlogPost p : posts)
			BlogService.get().deleteBlogPost(p);

		List<User> users = UserService.get().getUsers(organization);
		for (User u : users)
			UserService.get().deleteUser(u);

		List<Website> websites = HostingService.get().getWebsites(organization);
		for (Website w : websites) {
			List<ContentBinding> bindings = WebsiteService.get().getBindings(w);
			for (ContentBinding b : bindings) {
				WebsiteService.get().deleteContentBinding(b);
			}
			HostingService.get().deleteWebsite(w);
		}

		/*
		List<Cart> carts = OrderService.get().getOrders(organization);
		for (Cart c : carts)
			OrderService.get().deleteCart(c);
		
		List<Product> products = InventoryService.get().getProducts(organization);
		for (Product p : products)
			InventoryService.get().deleteProduct(p);
			*/

		List<Category> cats = CategoryService.get().getCategories(organization);
		for (Category c : cats)
			CategoryService.get().deleteCategory(c);

		try {
			em.getTransaction().begin();
			//organization.setSubscription(null);
			Organization tracked = em.merge(organization);

			em.remove(tracked);
			em.flush();
			em.getTransaction().commit();
			em.close();
		} catch (Exception e) {
			em.getTransaction().rollback();
			em.close();
			e.printStackTrace();
			throw new IllegalStateException("This organization has too much data to delete");
		}

	}

	public Organization saveOrganization(Organization organization) {
		EntityManager em = Simple.getEm();
		em.getTransaction().begin();
		Organization tracked = em.merge(organization);
		em.persist(tracked);
		em.flush();
		em.getTransaction().commit();
		em.close();
		return tracked;
	}

	/**
	 * Deprecated? What's this for, the setup getEditPanel maybe initially?
	 *
	 * Right now it checks for any organization in the system.
	 *
	 * @return
	 */
	public boolean hasRootOrganization() {
		Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());
		EntityManager em = Simple.getEm();
		try {
			em.createQuery("SELECT O FROM Organization O").getResultList().get(0);
			em.close();
			return true;
		} catch (NoResultException e) {
			em.close();
			return false;
		}

	}

	public Organization findUsersOrganization(User user) {
		return user.getOrganization();
	}

	public static OrganizationService get() {
		return Simple.get().getInjector().getProvider(OrganizationService.class).get();

	}

	public Organization getOrganization(String name) {
		EntityManager em = Simple.getEm();
		Map map = new HashMap<String, String>();
		map.put("name", name);
		return (Organization) em.createQuery("SELECT O FROM Organization O WHERE O.name=:name").setParameter("name", name).getResultList().get(0);
	}

	public List<Organization> getAllOrganizations() {
		EntityManager em = Simple.getEm();
		List<Organization> list = em.createQuery("SELECT O FROM Organization O").getResultList();
		em.close();
		return list;
	}

	public Organization getOrganizationById(long organization_id) {
		EntityManager em = Simple.getEm();
		Organization o = em.find(Organization.class, organization_id);
		em.close();
		return o;
	}
}
