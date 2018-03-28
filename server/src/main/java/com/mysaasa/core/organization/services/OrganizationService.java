package com.mysaasa.core.organization.services;

import com.mysaasa.core.users.model.User;
import com.mysaasa.interfaces.annotations.SimpleService;
import com.mysaasa.Simple;
import com.mysaasa.core.organization.model.Organization;
import org.slf4j.Logger;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;/* See the file "LICENSE" for the full license governing this code. */

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
	public void disableOrganization(Organization organization) throws IllegalStateException {
		//EntityManager em = Simple.getEntityManager();
		checkNotNull(organization);
		organization.setEnabled(false);
		saveOrganization(organization);
	}

	public Organization saveOrganization(Organization organization) {
		EntityManager em = Simple.getEntityManager();
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
		EntityManager em = Simple.getEntityManager();
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
		return Simple.getInstance().getInjector().getProvider(OrganizationService.class).get();

	}

	public Organization getOrganization(String name) {
		EntityManager em = Simple.getEntityManager();
		Map map = new HashMap<String, String>();
		map.put("name", name);
		return (Organization) em.createQuery("SELECT O FROM Organization O WHERE O.name=:name").setParameter("name", name).getResultList().get(0);
	}

	public List<Organization> getAllOrganizations() {
		EntityManager em = Simple.getEntityManager();
		List<Organization> list = em.createQuery("SELECT O FROM Organization O WHERE O.enabled IS NULL OR O.enabled=TRUE").getResultList();
		em.close();
		return list;
	}

	public Organization getOrganizationById(long organization_id) {
		EntityManager em = Simple.getEntityManager();
		Organization o = em.find(Organization.class, organization_id);
		em.close();
		return o;
	}
}
