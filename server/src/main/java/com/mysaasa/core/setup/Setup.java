package com.mysaasa.core.setup;

import com.google.common.collect.Lists;
import com.mysaasa.Simple;;
import com.mysaasa.core.blog.model.BlogPost;
import com.mysaasa.core.blog.services.BlogService;
import com.mysaasa.core.hosting.service.HostingService;
import com.mysaasa.core.organization.model.Organization;
import com.mysaasa.core.organization.services.OrganizationService;
import com.mysaasa.core.users.model.User;
import com.mysaasa.core.users.service.UserService;
import com.mysaasa.core.website.model.Website;
import com.mysaasa.messages.SetupMessage;
import com.mysaasa.pages.Splash;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.io.FileUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.extensions.ajax.markup.html.AjaxEditableLabel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;

import java.io.*;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

/**
 * Created by Adam on 3/24/14.
 */
public class Setup extends WebPage {
	private final WebMarkupContainer returnLink;
	private final FeedbackPanel feedback;
	private List<String> jdbc_options = Lists.asList("org.h2.Driver", new String[]{});

	private static class State implements Serializable {

		String url = "jdbc:h2:" + Simple.getConfigPath() + "database";
		String driverName = "org.h2.Driver";
		String db_username = "root";
		String db_password = "";
		String baseDomain = "simpletest.ca";
		String username = "admin";
		String password = "admin";
		String organization = "Default";

		String mail_host = "yourmailserver.host";
		String mail_port = "2525";
		String mail_user = "user@yourmailserver.host";
		String mail_password = "Your mail password";

		String port = "8080";

		public String getSecurePort() {
			return securePort;
		}

		public void setSecurePort(String securePort) {
			this.securePort = securePort;
		}

		String securePort = "8443";
		String gcmKey = "Your GCM Key here"; //https://console.cloud.google.com/apis/api -> Cloud Messaging For Android
		String keystorePassword = "password";

		public void writeToProperties() {
			Properties p = Simple.getProperties();
			p.setProperty(Simple.PREF_DB_PASS, db_password);
			p.setProperty(Simple.PREF_DB_USERNAME, db_username);
			p.setProperty(Simple.PREF_DB_DRIVER, driverName);
			p.setProperty(Simple.PREF_DB_URL, url);
			p.setProperty(Simple.PREF_BASE_DOMAIN, baseDomain);

			p.setProperty(Simple.PREF_MAIL_SMTP_HOST, mail_host);
			p.setProperty(Simple.PREF_MAIL_SMTP_PASSWORD, mail_password);
			p.setProperty(Simple.PREF_MAIL_SMTP_USER, mail_user);
			p.setProperty(Simple.PREF_MAIL_SMTP_PORT, mail_port);

			p.setProperty(Simple.PREF_PORT, port);
			p.setProperty(Simple.PREF_SECURE_PORT, securePort);
			p.setProperty(Simple.PREF_GCM_PROJECT_ID, gcmKey);

			p.setProperty(Simple.PREF_KEYSTORE_PASSWORD, keystorePassword);
			Simple.get().saveProperties();

		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public String getDriverName() {
			return driverName;
		}

		public void setDriverName(String driverName) {
			this.driverName = driverName;
		}

		public String getDb_username() {
			return db_username;
		}

		public void setDb_username(String db_username) {
			this.db_username = db_username;
		}

		public String getDb_password() {
			return db_password;
		}

		public void setDb_password(String db_password) {
			this.db_password = db_password;
		}

		public String getBaseDomain() {
			return baseDomain;
		}

		public void setBaseDomain(String baseDomain) {
			this.baseDomain = baseDomain;
		}

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getOrganization() {
			return organization;
		}

		public void setOrganization(String organization) {
			this.organization = organization;
		}
	}

	State state = new State();

	public Setup() {
		super();
		setDefaultModel(new CompoundPropertyModel<Object>(state));
		setOutputMarkupId(true);
		setWasCreatedBookmarkable(false);
		add(new AjaxEditableLabel<String>("url"));
		add(new DropDownChoice<>("driverName", jdbc_options));
		add(new AjaxEditableLabel<String>("db_username"));
		add(new AjaxEditableLabel<String>("db_password"));

		add(new AjaxEditableLabel<String>("baseDomain"));
		add(new AjaxEditableLabel<String>("username"));
		add(new AjaxEditableLabel<String>("password"));
		add(new AjaxEditableLabel<String>("organization"));

		add(new AjaxEditableLabel<String>("port"));
		add(new AjaxEditableLabel<String>("securePort"));
		add(new AjaxEditableLabel<String>("mail_port"));
		add(new AjaxEditableLabel<String>("mail_host"));
		add(new AjaxEditableLabel<String>("mail_user"));
		add(new AjaxEditableLabel<String>("mail_password"));
		add(new AjaxEditableLabel<String>("gcmKey"));

		add(feedback = new FeedbackPanel("feedback"));
		feedback.setOutputMarkupId(true);
		add(returnLink = new AjaxLink("continue") {
			@Override
			public void onClick(final AjaxRequestTarget target) {
				try {
					if (testDatabaseConnection()) {

						state.writeToProperties();
						send(getPage(), Broadcast.BREADTH, new SetupMessage() {
							@Override
							public AjaxRequestTarget getAjaxRequestTarget() {
								return target;
							}
						});

						installDefaultData();
						Setup.this.setResponsePage(Splash.class);
					}
				} catch (Exception e) {
					if (e.getMessage() != null) {
						error(e.getMessage());
					} else {
						e.printStackTrace();
						error(e.toString());
					}
					target.add(Setup.this);
				}
			}
		});

	}

	/**
	 * Test's the database connection, throws every exception ever
	 *
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws java.sql.SQLException
	 */
	private boolean testDatabaseConnection() throws ClassNotFoundException, IllegalAccessException, InstantiationException, SQLException {
		Class c = Class.forName(state.driverName);
		DriverManager.registerDriver((Driver) c.newInstance());
		Connection connection = DriverManager.getConnection(state.getUrl(), state.db_username, state.db_password);
		if (!connection.isValid(50)) {
			throw new IllegalArgumentException("Invalid Connection");
		}
		connection.close();
		return true;
	}

	/**
	 * Static methods and definitions.
	 * The access to the system preferences are here
	 */

	/**
	 *
	  */
	public void installIncludedThemes() throws IOException, ZipException {
		ClassLoader classloader = Thread.currentThread().getContextClassLoader();
		InputStream is = classloader.getResourceAsStream("bundled_sites.zip");
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream("bundled_sites.zip");
			byte[] buf = new byte[2048];
			int r = is.read(buf);
			while (r != -1) {
				fos.write(buf, 0, r);
				r = is.read(buf);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (fos != null) {
				fos.close();
			}
		}

		ZipFile zfile = new ZipFile("bundled_sites.zip");
		zfile.extractAll(Simple.getConfigPath() + "websites/");
		File f = new File("bundled_sites.zip");
		f.delete();
	}

	/**
	 * We need to install resources/bundled_sites into the websites folder, so they can be used/modified.
	 */
	private void installDefaultData() {
		if (new File(Simple.getConfigPath() + "websites/gettingstarted." + Simple.getBaseDomain() + "/").exists())
			return; //Short circuit for install
		try {
			installIncludedThemes();
			installDefaultCertificate();
			setupDefaultData();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ZipException e) {
			e.printStackTrace();
		}
	}

	private void installDefaultCertificate() throws IOException, ZipException {
		ClassLoader classloader = Thread.currentThread().getContextClassLoader();
		InputStream is = classloader.getResourceAsStream("debug.keystore.jks");
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(Simple.getConfigPath() + "/keystore.jks");
			byte[] buf = new byte[2048];
			int r = is.read(buf);
			while (r != -1) {
				fos.write(buf, 0, r);
				r = is.read(buf);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (fos != null) {
				fos.close();
			}
		}
	}

	private void setupDefaultData() throws IOException {

		Organization o = new Organization();
		o.setName(state.getOrganization());
		o = OrganizationService.get().saveOrganization(o);
		User u = new User(state.username, state.password, User.AccessLevel.ROOT);
		u.setOrganization(o);
		UserService.get().saveUser(u);

		info("Creating user");

		File f = new File(Simple.getConfigPath() + "websites");
		//Bundled Themes
		for (File f2 : f.listFiles()) {
			if (f2.isDirectory() && !f2.getName().contains(".")) {
				//Move directory

				String newName = f2.getParentFile().getPath() + "/" + f2.getName() + "." + state.baseDomain;
				FileUtils.copyDirectory(f2, new File(newName));
				FileUtils.deleteDirectory(f2);
				Website w = new Website();
				w.setOrganization(o);
				w.setProduction(f2.getName() + "." + state.baseDomain);
				w.setStaging(f2.getName() + "." + state.baseDomain);
				w.setVisible(false);
				HostingService.get().saveWebsite(w);
			}

		}

		BlogPost post = new BlogPost();
		post.setOrganization(o);
		post.setTitle("Sample Blog Post");
		post.setBody("Lorem Ipsum Dolor");
		post.setAuthor(UserService.get().getAllUsers().get(0));

		BlogService.get().saveBlogPost(post);

	}
}
