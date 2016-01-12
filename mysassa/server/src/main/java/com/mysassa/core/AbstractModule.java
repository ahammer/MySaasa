package com.mysassa.core;

import java.io.Serializable;
import java.util.Map;

import com.mysassa.core.users.model.User;
import com.mysassa.interfaces.IClassPanelAdapter;
import org.apache.wicket.Component;
import org.apache.wicket.event.IEvent;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

public abstract class AbstractModule implements Serializable {
	private static final long serialVersionUID = 3316812257078300314L;

	/**
	 * This is the title that will show in the menu
	 *
	 * @return the String title
	 */
	public abstract String getMenuTitle();

	/**
	 * When you hover a section a dropdown shows that provides actions and helpers.
	 * @param id
	 * @param model
	 * @return
	 */
	public abstract Component getMenuDropDownPanel(String id, IModel model);

	/**
	 * Each section has a sidebar, this is used for that.
	 *
	 * @param id
	 * @param model
	 * @return
	 */
	public abstract Component getSidebarPanel(String id, IModel model);

	/**
	 * This is the main window that takes 80% of the screen. It can switch, but when you click a title
	 * in the header, this will load by default, with the Default model.
	 *
	 * @param id
	 * @param model
	 * @return
	 */
	public abstract Component getMainPanel(String id, IModel model);

	/**
	 * Does a user of this level has access to this panel?
	 * @param UserAccessLevel
	 *
	 * @return
	 */
	public abstract boolean hasAccess(User.AccessLevel UserAccessLevel);

	/**
	 * This helper is used as a fallback to get a default blank model for a new object under the type of the module.
	 *
	 * E.g.
	 *
	 * Model of type Website for the Hosting Module.
	 * @return
	 */
	public abstract Model getDefaultModel();

	/**
	 * Each module is responsible for adapting models to panels for classes they control.
	 *
	 * When a EditEvent is sent, the adapters will be checked and if a suitable one is found
	 * for that type of object exists.
	 *
	 * @return A map of Class Panel Adapters with the key type being the object's Class.
	 */
	public abstract Map<Class, IClassPanelAdapter> getClassPanelAdapters();

	public void onEvent(IEvent<?> event) {}

}
