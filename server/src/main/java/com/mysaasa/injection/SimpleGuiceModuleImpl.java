package com.mysaasa.injection;

import com.google.inject.CreationException;
import com.mysaasa.core.hosting.service.BaseInjectedService;
import com.mysaasa.interfaces.annotations.SimpleService;
import com.mysaasa.Simple;
import com.mysaasa.SimpleImpl;
import org.reflections.*;
import org.slf4j.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * The Default Injector used to Create Services and Modules and such.
 *
 * Created by adam on 12/24/13.
 */
public class SimpleGuiceModuleImpl extends AbstractSimpleGuiceModule {
	private final Reflections reflections;
	private EntityManagerFactory emf = null;

	public SimpleGuiceModuleImpl() {
		try {
			reflections = new Reflections("com.mysaasa");
		} catch (NoClassDefFoundError e) {
			throw new RuntimeException("Could not run Service Detector, SimpleGuicemoduleImpl", e);
		}
	}

	@Override
	protected void configure() {

		Set<Class> bound = new HashSet<>();
		for (Class c : reflections.getTypesAnnotatedWith(SimpleService.class)) {
			Class abstractParent = findAbstractParent(c);
			if (bound.contains(c) || bound.contains(abstractParent))
				continue;
			if (abstractParent == Object.class)
				throw new RuntimeException(abstractParent + "->" + c);
			try {
				SimpleService ss = (SimpleService) c.getAnnotations()[0];
				if (abstractParent != c) {
					bound.add(abstractParent);
					bind(abstractParent).toProvider(new ServiceProvider(c.newInstance())).asEagerSingleton();
				} else {
					bound.add(c);
					this.bind(c).toProvider(new ServiceProvider(c.newInstance())).asEagerSingleton();
				}
			} catch (CreationException e) {
				throw new RuntimeException("Binding:" + abstractParent + " " + c, e);
			} catch (InstantiationException e) {
				throw new RuntimeException("Could not Instantiate a class", e);
			} catch (IllegalAccessException e) {
				throw new RuntimeException("Could not Access Class", e);
			}
		}
	}

	private Class findAbstractParent(Class c) {
		if (c.getSuperclass() == Object.class)
			return c;
		return c.getSuperclass();
	}

	@Override
	public Logger providesLogger() {
		return SimpleImpl.getInstance().getLogger();
	}

	@Override
	public EntityManager providesEntityManager() {
		if (emf == null) {
			try {
				Map<String, String> properties = Simple.getInstance().getEntityManagerFactoryPropertyMap();
				emf = Persistence.createEntityManagerFactory("Simple", properties);
			} catch (IllegalStateException e) {
				emf = null;
				return null;
			}
		}

		EntityManager em = emf.createEntityManager();
		Long t2 = System.nanoTime();
		return em;
	}

	public void linkServices() {
		for (Class c : reflections.getSubTypesOf(BaseInjectedService.class)) {
			((BaseInjectedService) Simple.getInstance().getInjector().getProvider(c).get()).inject();
		}
	}
}
