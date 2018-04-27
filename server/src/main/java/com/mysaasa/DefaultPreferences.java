package com.mysaasa;

import org.apache.wicket.request.cycle.RequestCycle;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class DefaultPreferences {
    public static final String PREF_DB_DRIVER = "databaseDriver";
    public static final String PREF_DB_URL = "databaseUrl";
    public static final String PREF_DB_USERNAME = "databaseUsername";
    public static final String PREF_DB_PASS = "databasePassword";
    public static final String PREF_MAIL_SMTP_HOST = "mail.smtp.host";
    public static final String PREF_MAIL_SMTP_PORT = "mail.smtp.port";
    public static final String PREF_MAIL_SMTP_USER = "mail.smtp.user";
    public static final String PREF_MAIL_SMTP_PASSWORD = "mail.smtp.password";
    public static final String PREF_PORT = "port";
    public static final String PREF_SECURE_PORT = "secure.port";
    public static final String PREF_GCM_PROJECT_ID = "GCM.KEY";
    public static final String PREF_KEYSTORE_PASSWORD = "keystore.password";
    private static final String PATH_ENVIRONMENT_VARIABLE = "MYSAASA_PATH";
    private static final String PATH_DEFAULT_NIX = "/opt/mysaasa/";
    private static final String PATH_DEFAULT_WIN = "C:\\opt\\mysaasa\\";
    public static final Object SETTINGS_FILE = "settings.properties";
    static final String PREF_CONTACT_EMAIL = "contactEmail";
    private static Properties PROPERTIES = null;

    /**
     * This is the Port gotten from the "port" property of the settings.properties file.
     * The default is 80, but it can be over-ridden in the file
     *
     * @return the non https port
     */
    public static int getPort() {
        return Integer.parseInt(getProperties().getProperty(PREF_PORT, "8080"));
    }

    public static int getSecurePort() {
        return Integer.parseInt(getProperties().getProperty(PREF_SECURE_PORT, "443"));
    }

    public static String getContactEmail() {
        return getProperties().getProperty(PREF_CONTACT_EMAIL, null);
    }

    public static String getKeystorePassword() {
        return getProperties().getProperty(PREF_KEYSTORE_PASSWORD, "password");
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
        return Boolean.valueOf(getProperties().getProperty("localDevMode", "false"));
    }

    /**
     * Location of the configs, either the SIMPLE_PLATFORM_PATH environment variable or
     * /opt/simple as a default
     *
     * @return the config path
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
     * @return the properties
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

    public static String getCurrentDomain() {
        return RequestCycle.get().getRequest().getClientUrl().getHost();
    }
}
