package com.mysaasa;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.mysaasa.core.AbstractModule;
import com.mysaasa.core.ModuleManager;
import com.mysaasa.core.setup.Setup;
import com.mysaasa.development.CodeGen;
import com.mysaasa.injection.MySaasaModule;
import com.mysaasa.interfaces.IClassPanelAdapter;
import com.mysaasa.pages.Splash;
import com.mysaasa.pages.docs.api.ApiGuide;
import com.mysaasa.pages.docs.template.TemplateGuide;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.https.HttpsConfig;
import org.slf4j.Logger;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * This is the entry point for the application
 */
public class MySaasa extends WebApplication {
	public static boolean IN_MEMORY_DATABASE;
	protected final Logger logger = org.slf4j.LoggerFactory.getLogger(MySaasa.class);
	private MySaasaModule guiceModule;
	private final Injector injector = Guice.createInjector(guiceModule = new MySaasaModule());

	private ModuleManager moduleManager;
	private boolean initialized = false;

	public MySaasa()  {
		if (MySaasaDaemon.isLocalMode()) {
			setOfflineMode(true);
		}
	}


	/**
	 * Injection typing shortcut
	 * @param instance
	 */
	public static void inject(Object instance) {
		((MySaasa)WebApplication.get()).getInjector().injectMembers(instance);
	}

	public static MySaasa getInstance() {
		return ((MySaasa)WebApplication.get());
	}

	public static <T> T getService(Class<T> type) {
		return getInstance().getInjector().getProvider(type).get();
	}


	@Override
	public void init() {
		super.init();
		moduleManager = ModuleManager.get();
		guiceModule.linkServices(injector);

		getMarkupSettings().setStripWicketTags(true);
		// IMPORTANT!
		mountPage("/Admin", Splash.class);
		mountPage("/ApiGuide", ApiGuide.class);
		mountPage("/TemplateGuide", TemplateGuide.class);

		getRootRequestMapperAsCompound().add(new MysaasaRequestMapper());
		HttpsConfig config = new HttpsConfig();
		config.setHttpsPort(DefaultPreferences.getSecurePort());

		CodeGen.generateRetrofitCode();
		new SSLGen().doSSLMagic();
		initialized = true;
	}

	public Injector getInjector() {
		return injector;
	}

	public boolean isInitialized() {
		return initialized;
	}

	/**
	 * Checks the two properties for setup, userInitialized and dbInitialized
	 * These are optimizations
	 *
	 * @return True if setup correctly, with a root user/organization and
	 */
	private boolean hasBeenInstalled() {
		Properties p = DefaultPreferences.getProperties();
		return p.keySet().size() > 0;
	}



	private String getPropertiesFilePath() {
		return DefaultPreferences.getConfigPath() + "/" + DefaultPreferences.SETTINGS_FILE;
	}

	/**
	 *  Saves the properties files, if you have modified them call this and it'll write the new ones to disk.
	 */
	public void saveProperties() {
		try {
			DefaultPreferences.getProperties().store(new FileOutputStream(getPropertiesFilePath()), "Generated by Setup - Don't touch if you don't know why");
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Can not save the properties file", e);
		}
	}

	@Override
	public void onEvent(IEvent<?> event) {
		for (final AbstractModule m : moduleManager.getModules()) {
			m.onEvent(event);
		}
	}

	@Override
	public Class<? extends WebPage> getHomePage() {

		if (hasBeenInstalled()) {
			return null;
		} else {
			return Setup.class;
		}

	}

	public static IClassPanelAdapter getClassPanelAdapter(Class aClass) {
		return ((MySaasa)WebApplication.get()).moduleManager.getClassPanelAdapter(aClass);
	}

	//Can set before this starts
	protected void setOfflineMode(boolean b) {
		this.IN_MEMORY_DATABASE = b;
	}
}
