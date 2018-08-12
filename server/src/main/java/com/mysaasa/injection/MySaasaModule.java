package com.mysaasa.injection;

import com.google.inject.CreationException;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.Provides;
import com.mysaasa.DefaultPreferences;
import com.mysaasa.MySaasa;
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
			throw new RuntimeException("Could not run Service Detector", e);
		}
	}

	@Override
	protected void configure() {

		Set<Class> bound = new HashSet<>();
		// Phase 1 Create Appropriate Services
		for (Class _class : reflections.getTypesAnnotatedWith(SimpleService.class)) {
			Class abstractParent = findAbstractParent(_class);

			// If a class is already bound, we can skip this one
			if (bound.contains(_class) || bound.contains(abstractParent))
				continue;

			// Deprecated? Not sure why we have this
			if (abstractParent == Object.class)
				throw new RuntimeException(abstractParent + "->" + _class);

			// Look up the service, and bind it in the module
			try {
				SimpleService service = (SimpleService) _class.getAnnotations()[0];
				if (abstractParent != _class) {
					// Always bind by the Abstract parents class name, not the implementation
					bound.add(abstractParent);
					bind(abstractParent).toProvider(new ServiceProvider(_class.newInstance())).asEagerSingleton();
				} else {
					bound.add(_class);
					this.bind(_class).toProvider(new ServiceProvider(_class.newInstance())).asEagerSingleton();
				}
			} catch (CreationException e) {
				throw new RuntimeException("Binding:" + abstractParent + " " + _class, e);
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

		// Could be reduced with cohesion of arguments
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

	public void linkServices(Injector injector) {
		Set<Class<?>> subTypesOf = reflections.getTypesAnnotatedWith(SimpleService.class);

		for (Class aClass : subTypesOf) {
			Provider provider = injector.getProvider(aClass);

			// If Da
			if (provider != null) {
				Object target = provider.get();
				injector.injectMembers(target);
			}
		}
	}
}
