package com.mysaasa.core.setup;

import com.mysaasa.DefaultPreferences;
import com.mysaasa.MySaasa;
import com.mysaasa.messages.SetupMessage;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;

import javax.persistence.EntityManager;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
* Created by Adam on 4/11/14.
*/
public class SetupJpaForm extends Form {

	private final WebMarkupContainer ready;

	@Override
	protected void onConfigure() {
		super.onConfigure();
		//ready.setVisible(Boolean.valueOf(MySaasa.getInstance().getProperties().getProperty(MySaasa.getInstance().PREF_JPA_INITIALIZED, "false")));
	}

	public static class SetupJpaFormData implements Serializable {
		private String url = "jdbc:h2:" + DefaultPreferences.getConfigPath() + "database";
		private String name = "root";
		private String password = "";
		private String driverName = "org.h2.Driver";

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getPassword() {
			if (password == null)
				return "";
			return password;

		}

		public void setPassword(String password) {
			this.password = password;
		}

		public String getDriverName() {
			return driverName;
		}

		public void setDriverName(String driverName) {
			this.driverName = driverName;
		}
	}

	static final SetupJpaFormData data = new SetupJpaFormData();

	public SetupJpaForm(String id) {
		super(id, new CompoundPropertyModel(data));
		add(ready = new WebMarkupContainer("ready"));
		add(new TextField<>("url").setRequired(true));
		add(new TextField<>("name").setRequired(false));

		DropDownChoice<String> ddc = new DropDownChoice<String>("driverName",
				new PropertyModel<String>(data, "driverName"),
				new LoadableDetachableModel<List<String>>() {
					@Override
					protected List<String> load() {
						return Arrays.asList(new String[]{"org.h2.Driver", "com.mysql.jdbc.Driver"});
					}
				});
		add(ddc);
		add(new PasswordTextField("password").setRequired(false));

		final FeedbackPanel feedback;
		add(feedback = new FeedbackPanel("feedback"));
		feedback.setOutputMarkupId(true);
		add(new AjaxSubmitLink("submit", this) {
			protected void onSubmit(final org.apache.wicket.ajax.AjaxRequestTarget target, Form<?> form) {
				try {
					testDatabaseConnection();

					Properties p = DefaultPreferences.getProperties();
					p.put(DefaultPreferences.PREF_DB_DRIVER, data.getDriverName());
					p.put(DefaultPreferences.PREF_DB_USERNAME, data.getName());
					p.put(DefaultPreferences.PREF_DB_PASS, data.getPassword());
					p.put(DefaultPreferences.PREF_DB_URL, data.getUrl());
					//p.put(MySaasa.getInstance().PREF_JPA_INITIALIZED, "true");
					MySaasa.getInstance().saveProperties();
					//Should be able to initialize database now
					EntityManager em = MySaasa.getInstance().getInjector().getProvider(EntityManager.class).get();
					int size = em.createQuery("SELECT U FROM User U").getResultList().size();

					if (size > 0) {
						//  p.put(MySaasa.getInstance().PREF_USER_INITIALIZED, String.valueOf(true));
						MySaasa.getInstance().saveProperties();
					}

					info("Success");
				} catch (Exception e) {
					e.printStackTrace();
					error("There was a error");

				}
				send(getPage(), Broadcast.BREADTH, new SetupMessage() {
					@Override
					public AjaxRequestTarget getAjaxRequestTarget() {
						return target;
					}
				});
			}

			protected void onError(org.apache.wicket.ajax.AjaxRequestTarget target, Form<?> form) {
				target.add(feedback);
			}
		});

		add(new AjaxSubmitLink("test", this) {
			protected void onSubmit(final org.apache.wicket.ajax.AjaxRequestTarget target, Form<?> form) {
				try {
					testDatabaseConnection();
					info("Success");
				} catch (Exception e) {
					e.printStackTrace();
					error("There was a error");
					error(e.getMessage());
				}
				send(getPage(), Broadcast.BREADTH, new SetupMessage() {
					@Override
					public AjaxRequestTarget getAjaxRequestTarget() {
						return target;
					}
				});
			}

			protected void onError(org.apache.wicket.ajax.AjaxRequestTarget target, Form<?> form) {
				target.add(feedback);
			}

		});
	}

	/**
	 * Test's the database connection, throws every exception ever
	 *
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws SQLException
	 */
	private boolean testDatabaseConnection() throws ClassNotFoundException, IllegalAccessException, InstantiationException, SQLException {
		Class c = Class.forName(data.getDriverName());
		DriverManager.registerDriver((Driver) c.newInstance());
		Connection connection = DriverManager.getConnection(data.getUrl(), data.getName(), data.getPassword());
		if (!connection.isValid(50)) {
			throw new IllegalArgumentException("Invalid Connection");
		}
		connection.close();
		return true;
	}

}
