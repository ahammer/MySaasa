package com.mysassa.core.editor;

import com.mysassa.core.editor.panels.Ace;
import com.mysassa.core.editor.panels.EditorHeader;
import com.mysassa.core.users.model.User;
import com.mysassa.interfaces.AbstractClassPanelAdapter;
import com.mysassa.interfaces.IClassPanelAdapter;
import com.mysassa.core.AbstractModule;
import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by administrator on 2014-05-19.
 */
public class EditorModule extends AbstractModule {
	@Override
	public String getMenuTitle() {
		return null;
	}

	@Override
	public Component getMenuDropDownPanel(String id, IModel model) {
		return new EditorHeader(id, new Model("empty"));
	}

	public Component getSidebarPanel(String id, IModel model) {
		return null;
	}

	@Override
	public Component getMainPanel(String id, IModel model) {
		return new Ace(id, (Model<File>) model);
	}

	@Override
	public boolean hasAccess(User.AccessLevel UserAccessLevel) {
		return true;
	}

	@Override
	public Model getDefaultModel() {
		return new Model("Not Implemented");
	}

	@Override
	public Map<Class, IClassPanelAdapter> getClassPanelAdapters() {
		HashMap<Class, IClassPanelAdapter> map = new HashMap<>();
		map.put(File.class, new AbstractClassPanelAdapter<File>() {
			@Override
			public Component getEditPanel(String id, File o) {
				return new Ace(id, new Model((File) o));
			}

			@Override
			public boolean isFullscreen() {
				return true;
			}
		});
		return map;
	}
}
