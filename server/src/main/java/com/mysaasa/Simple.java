package com.mysaasa;

import com.google.inject.Injector;

import com.mysaasa.core.ModuleManager;
import com.mysaasa.core.setup.Setup;
import com.mysaasa.development.CodeGen;
import com.mysaasa.injection.SimpleGuiceModuleImpl;
import com.mysaasa.interfaces.IClassPanelAdapter;
import com.mysaasa.pages.Splash;
import com.mysaasa.pages.docs.api.ApiGuide;
import com.mysaasa.pages.docs.template.TemplateGuide;
import com.mysaasa.core.AbstractModule;

import org.apache.wicket.event.IEvent;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.https.HttpsConfig;
import org.slf4j.Logger;

import javax.persistence.EntityManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * The "global" portion of the application.
 * There is only one instance, and it stores some configs and statics that are global
 *
 * This class only bootstraps the application, and deals with global config helpers
 *
 * Created by Adam on 3/30/14.
 */
public abstract class Simple extends WebApplication {
	private static Simple INSTANCE;
	public static boolean BitcoinEnabled = true;
	public static final String PREF_DB_DRIVER = "databaseDriver";
	public static final String PREF_DB_URL = "databaseUrl";
	public static final String PREF_DB_USERNAME = "databaseUsername";
	public static final String PREF_DB_PASS = "databasePassword";
	public static final String PREF_BASE_DOMAIN = "baseDomain";

	public static final String PREF_MAIL_SMTP_HOST = "mail.smtp.host";
	public static final String PREF_MAIL_SMTP_PORT = "mail.smtp.port";
	public static final String PREF_MAIL_SMTP_USER = "mail.smtp.user";
	public static final String PREF_MAIL_SMTP_PASSWORD = "mail.smtp.password";

	public static final String PREF_PORT = "port";
	public static final String PREF_SECURE_PORT = "secure.port";
	public static final String PREF_GCM_PROJECT_ID = "GCM.KEY";
	public static final String PREF_KEYSTORE_PASSWORD = "keystore.password";

	public static final String PATH_ENVIRONMENT_VARIABLE = "MYSAASA_PATH";
	private static final String PATH_DEFAULT_NIX = "/opt/mysaasa/";
	private static final String PATH_DEFAULT_WIN = "C:\\opt\\mysaasa\\";
	public static final Object SETTINGS_FILE = "settings.properties";
	public static Properties PROPERTIES = null;
	protected SimpleGuiceModuleImpl simpleGuiceModule;
	protected final Logger logger = org.slf4j.LoggerFactory.getLogger(SimpleImpl.class);
	private ModuleManager moduleManager;

	/**
	 * Such a simple, no args-constructor.
	 */
	public Simple() {
		INSTANCE = this;
	}

	/**
	 * Get's this, there is only one Application class, and it's a singleton, so this is access
	 * to the cast version of it. Ultimately it's a convenience so we don't need to do unsafe
	 * casts everywhere.
	 *
	 * @return
	 */
	public static Simple get() {
		return INSTANCE;
	}

	/**
	 * This is a shortcut to the EntityManager
	 *
	 * @return
	 */
	public static EntityManager getEm() {
		return Simple.get().getInjector().getProvider(EntityManager.class).get();
	}

	/**
	 * This is the Port gotten from the "port" property of the settings.properties file.
	 * The default is 80, but it can be over-ridden in the file
	 *
	 * @return
	 */
	public static int getPort() {
		return Integer.parseInt(getProperties().getProperty(PREF_PORT, "8080"));
	}

	public static int getSecurePort() {
		return Integer.parseInt(getProperties().getProperty(PREF_SECURE_PORT, "443"));
	}

	/**
	 * This is the Base domain, it's configured in the settings.properties, and it's the root central hosting.
	 *
	 * The admin should be accessible here and any static assets.
	 *
	 * @return
	 */
	public static String getBaseDomain() {
		if (!getProperties().containsKey("baseDomain"))
			throw new RuntimeException("Put a baseDomain in settings.properties, it specifies the root for shared contentca");
		return getProperties().getProperty("baseDomain", null);
	}

	/**
	 * Get's the default setting/config path for the OS
	 * There is one for windows and another for linux/mac
	 *
	 * @return PATH_DEFAULT_WIN for windows, PATH_DEFAULT_NIX for other
     */
	public static String getPathDefault() {
		String osName = System.getProperty("os.name");
		boolean isWindows = osName.toLowerCase().contains("windows");
		return isWindows ? PATH_DEFAULT_WIN : PATH_DEFAULT_NIX;
	}

	/**
	 * Check to see if Local Dev Mode is enabled
	 *
	 * When local dev mode is enabled, we use a simplified edit mode
	 * that can work with the localhost file and not wildcard domains
	 *
	 * @return True if in Local Mode, Otherwise False
     */
	public static boolean isLocalDevMode() {
		return Boolean.valueOf(getProperties().getProperty("localDevMode","false"));
	}



	/**
	 * Dependency injection yay.
	 *
	 * @return
	 */
	public abstract Injector getInjector();

	/**
	 * Global logging instance
	 *
	 */
	@Deprecated
	public Logger getLogger() {
		return logger;
	}

	/**
	 * Location of the configs, either the SIMPLE_PLATFORM_PATH environment variable or
	 * /opt/simple as a default
	 *
	 * @return
	 */
	public static String getConfigPath() {

		try {
			String path = System.getenv(PATH_ENVIRONMENT_VARIABLE).replace('\\', '/');
			if (path != null && path.endsWith("/"))
				return path;
			if (path != null)
				return path + "/";
		} catch (Exception e) {
			//Use default
		}
		return getPathDefault().replace('\\', '/');

	}

	/**
	 *
	 * Get the global properties for you, create if it doesn't exist.
	 *
	 * @return
	 * @throws java.io.IOException
	 */
	public static Properties getProperties() {
		File propertiesFile = new File(getConfigPath() + "/" + SETTINGS_FILE);
		try {
			if (PROPERTIES == null) {

				new File(getConfigPath()).mkdirs();

				if (!propertiesFile.exists()) {
					propertiesFile.createNewFile();
				}
				PROPERTIES = new Properties();
				PROPERTIES.load(new FileInputStream(propertiesFile));
			}

			if (PROPERTIES == null)
				throw new RuntimeException("Can't load properties: " + propertiesFile.getAbsolutePath());
			return PROPERTIES;
		} catch (IOException e) {
			throw new RuntimeException("Can not load or create the properies file " + propertiesFile.getAbsolutePath());
		}
	}

	/**
	 * Checks the two properties for setup, userInitialized and dbInitialized
	 * These are optimizations
	 *
	 * @return True if setup correctly, with a root user/organization and
	 */
	public boolean hasBeenInstalled() {
		Properties p = getProperties();

		return p.keySet().size() > 0;
	}

	public Map<String, String> getEntityManagerFactoryPropertyMap() {
		if (getProperties().getProperty(PREF_DB_URL) == null || getProperties().getProperty(PREF_DB_PASS) == null || getProperties().getProperty(PREF_DB_USERNAME) == null || getProperties().getProperty(PREF_DB_DRIVER) == null) {
			throw new IllegalStateException("Database has not been set up yet");
		}

		Map<String, String> map = new HashMap<>();

		//Could be reduced with cohesion of arguments
		String url = getProperties().getProperty(PREF_DB_URL);
		String driver = getProperties().getProperty(PREF_DB_DRIVER);
		String username = getProperties().getProperty(PREF_DB_USERNAME);
		String password = getProperties().getProperty(PREF_DB_PASS);
		map.put("javax.persistence.jdbc.url", url);
		map.put("javax.persistence.jdbc.driver", driver);
		map.put("javax.persistence.jdbc.user", username);
		map.put("javax.persistence.jdbc.password", password);
		return map;
	}

	private String getPropertiesFilePath() {
		return getConfigPath() + "/" + SETTINGS_FILE;
	}

	/**
	 *  Saves the properties files, if you have modified them call this and it'll write the new ones to disk.
	 */
	public void saveProperties() {
		try {
			getProperties().store(new FileOutputStream(getPropertiesFilePath()), "Generated by Setup - Don't touch if you don't know why");
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Can not save the properties file", e);
		}
	}

	@Override
	public void init() {
		super.init();
		moduleManager = new ModuleManager();
		getMarkupSettings().setStripWicketTags(true); // IMPORTANT!
		mountPage("/ApiGuide", ApiGuide.class);
		mountPage("/TemplateGuide", TemplateGuide.class);

		getRootRequestMapperAsCompound().add(new SimpleRequestMapper());
		HttpsConfig config = new HttpsConfig();
		config.setHttpsPort(getSecurePort());

		CodeGen.generateRetrofitCode();
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
			return Splash.class;
		} else {
			return Setup.class;
		}

	}

	public IClassPanelAdapter getClassPanelAdapter(Class aClass) {
		return moduleManager.getClassPanelAdapter(aClass);
	}

}
