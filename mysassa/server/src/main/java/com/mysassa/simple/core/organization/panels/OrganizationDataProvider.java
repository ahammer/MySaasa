package com.mysassa.simple.core.organization.panels;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.mysassa.simple.Simple;
import com.mysassa.simple.core.organization.model.Organization;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

public class OrganizationDataProvider extends SortableDataProvider<Organization, String> {
	private static final long serialVersionUID = 1L;

	public OrganizationDataProvider() {}

	@Override
	public Iterator<? extends Organization> iterator(long first, long count) {
		new ArrayList<Organization>();
		EntityManager em = Simple.getEm();
		final Iterator<? extends Organization> result = em.createQuery("SELECT O FROM Organization O").getResultList().subList((int) first, (int) (first + count)).iterator();
		em.close();
		return result;
	}

	@Override
	public IModel<Organization> model(Organization object) {
		return new CompoundPropertyModel(object);
	}

	@Override
	public long size() {
		EntityManager em = Simple.getEm();

		if (em == null)
			throw new IllegalStateException("No available entity manager");
		Query q = em.createQuery("SELECT O FROM Organization O");
		if (q == null)
			throw new IllegalStateException("Could not generate query");
		List l = q.getResultList();
		if (l == null)
			throw new IllegalStateException("Could not get result list");
		em.close();
		return l.size();
	}
}
