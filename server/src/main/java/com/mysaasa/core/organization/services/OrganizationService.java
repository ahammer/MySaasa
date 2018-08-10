package com.mysaasa.core.organization.services;

import com.mysaasa.MySaasa;
import com.mysaasa.core.users.model.User;
import com.mysaasa.core.users.service.UserService;
import com.mysaasa.interfaces.annotations.SimpleService;
import com.mysaasa.core.organization.model.Organization;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;/* See the file "LICENSE" for the full license governing this code. */

@SimpleService
public class OrganizationService {

	@Inject
	EntityManager em;

	@Inject
	UserService userService;

	public OrganizationService() {
		super();
	}

	/**
	 * TODO Maybe inline some of this and make it one big transaction
	 *
	 * @param organization org
	 * @throws IllegalStateException exception
	 */
	public void disableOrganization(Organization organization) throws IllegalStateException {
		checkNotNull(organization);
		organization.setEnabled(false);
		saveOrganization(organization);
	}

	public Organization saveOrganization(Organization organization) {
		em.getTransaction().begin();
		Organization tracked = em.merge(organization);
		em.persist(tracked);
		em.flush();
		em.getTransaction().commit();

		return tracked;
	}

	/**
	 * Deprecated? What's this for, the setup getEditPanel maybe initially?
	 *
	 * Right now it checks for any organization in the system.
	 *
	 * @return return
	 */
	public boolean hasRootOrganization() {
		Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());

		try {
			em.createQuery("SELECT O FROM Organization O").getResultList().get(0);

			return true;
		} catch (NoResultException e) {

			return false;
		}

	}

	public Organization findUsersOrganization(User user) {
		return user.getOrganization();
	}

	public static OrganizationService get() {
		return MySaasa.getInstance().getInjector().getProvider(OrganizationService.class).get();

	}

	public Organization getOrganization(String name) {
		Map map = new HashMap<String, String>();
		map.put("name", name);
		return (Organization) em.createQuery("SELECT O FROM Organization O WHERE O.name=:name").setParameter("name", name).getResultList().get(0);
	}

	public List<Organization> getAllOrganizations() {
		List<Organization> list = em.createQuery("SELECT O FROM Organization O WHERE O.enabled IS NULL OR O.enabled=TRUE").getResultList();

		return list;
	}

	public Organization getOrganizationById(long organization_id) {
		Organization o = em.find(Organization.class, organization_id);

		return o;
	}
}
