package com.mysassa.core.users.panels;

import java.util.ArrayList;
import java.util.Iterator;

import javax.persistence.EntityManager;

import com.mysassa.Simple;;
import com.mysassa.core.users.model.User;

import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

public class UsersDataProvider extends SortableDataProvider<User, String> {
	private static final long serialVersionUID = 1L;

	public UsersDataProvider() {}

	@Override
	public Iterator<? extends User> iterator(long first, long count) {
		new ArrayList<User>();
		EntityManager em = Simple.getEm();
		final Iterator<? extends User> result = em.createQuery("SELECT U FROM User U").getResultList().subList((int) first, (int) (first + count)).iterator();
		em.close();
		return result;
	}

	@Override
	public IModel<User> model(User object) {
		return new CompoundPropertyModel(object);
	}

	@Override
	public long size() {
		new ArrayList<User>();
		EntityManager em = Simple.getEm();
		final int result = em.createQuery("SELECT U FROM User U").getResultList().size();
		em.close();
		return result;
	}
}
