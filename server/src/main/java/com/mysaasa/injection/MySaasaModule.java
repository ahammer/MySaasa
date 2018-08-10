package com.mysaasa.injection;

import com.google.inject.CreationException;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.Provides;
import com.mysaasa.DefaultPreferences;
import com.mysaasa.MySaasa;
import com.mysaasa.core.hosting.service.BaseInjectedService;
import com.mysaasa.interfaces.annotations.SimpleService;
import org.reflections.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.mysaasa.MySaasa.IN_MEMORY_DATABASE;

/**
 * The Default Injector used to Create Services and Modules and such.
 *
 * Created by adam on 12/24/13.
 */
public class MySaasaModule extends com.google.inject.AbstractModule {
	private final Reflections reflections;
	private EntityManagerFactory emf = null;

	public MySaasaModule() {
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

	Logger logger = LoggerFactory.getLogger(MySaasa.class);
	@Provides
	public Logger providesLogger() {
		return logger;
	}

	@Provides
	public EntityManager providesEntityManager() {
		if (emf == null) {
			try {
				Map<String, String> properties = getEntityManagerFactoryPropertyMap();
				emf = Persistence.createEntityManagerFactory("MySaasa", properties);
			} catch (IllegalStateException e) {
				emf = null;
				return null;
			}
		}

		EntityManager em = emf.createEntityManager();
		Long t2 = System.nanoTime();
		return em;
	}

	private Map<String, String> getEntityManagerFactoryPropertyMap() {
		if (DefaultPreferences.getProperties().getProperty(DefaultPreferences.PREF_DB_URL) == null || DefaultPreferences.getProperties().getProperty(DefaultPreferences.PREF_DB_PASS) == null || DefaultPreferences.getProperties().getProperty(DefaultPreferences.PREF_DB_USERNAME) == null || DefaultPreferences.getProperties().getProperty(DefaultPreferences.PREF_DB_DRIVER) == null) {
			throw new IllegalStateException("Database has not been set up yet");
		}

		Map<String, String> map = new HashMap<>();

		//Could be reduced with cohesion of arguments
		String url = DefaultPreferences.getProperties().getProperty(DefaultPreferences.PREF_DB_URL);
		String driver = DefaultPreferences.getProperties().getProperty(DefaultPreferences.PREF_DB_DRIVER);
		String username = DefaultPreferences.getProperties().getProperty(DefaultPreferences.PREF_DB_USERNAME);
		String password = DefaultPreferences.getProperties().getProperty(DefaultPreferences.PREF_DB_PASS);

		if (IN_MEMORY_DATABASE) {
			url = "jdbc:h2:mem:";
		}

		map.put("javax.persistence.jdbc.url", url);
		map.put("javax.persistence.jdbc.driver", driver);
		map.put("javax.persistence.jdbc.user", username);
		map.put("javax.persistence.jdbc.password", password);
		return map;
	}
	public void linkServices() {
		Set<Class<? extends BaseInjectedService>> subTypesOf = reflections.getSubTypesOf(BaseInjectedService.class);

		for (Class aClass : subTypesOf) {
			MySaasa instance = MySaasa.getInstance();
			Injector injector = instance.getInjector();
			Provider provider = injector.getProvider(aClass);
			BaseInjectedService baseInjectedService = (BaseInjectedService) provider.get();
			baseInjectedService.inject();
		}
	}
}
