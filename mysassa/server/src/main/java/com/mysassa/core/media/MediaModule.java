package com.mysassa.core.media;

import com.mysassa.core.media.panels.Header;
import com.mysassa.core.media.model.Media;
import com.mysassa.core.media.panels.MediaAdmin;
import com.mysassa.core.media.panels.uploader.Uploader;
import com.mysassa.core.users.model.User;
import com.mysassa.interfaces.IClassPanelAdapter;
import org.apache.wicket.Component;

import com.mysassa.core.AbstractModule;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MediaModule extends AbstractModule {
	private static final long serialVersionUID = 1L;

	@Override
	public boolean hasAccess(User.AccessLevel UserAccessLevel) {
		switch (UserAccessLevel) {
		case ORG:
		case WWW:
		case ROOT:
			return true;
		default:
			return false;
		}
	}

	@Override
	public Model getDefaultModel() {
		return new Model(new Media());
	}

	@Override
	public Map<Class, IClassPanelAdapter> getClassPanelAdapters() {
		HashMap<Class, IClassPanelAdapter> result = new HashMap();
		return result;
	}

	@Override
	public Component getMenuDropDownPanel(String id, IModel model) {
		return new Header(id);
	}

	@Override
	public Component getSidebarPanel(String id, IModel model) {
		return new MediaAdmin(id);
	}

	@Override
	public Component getMainPanel(String id, IModel model) {
		return new Uploader(id) {
			@Override
			public void done(AjaxRequestTarget target, List<Media> uploads) {

			}
		};
	}

	@Override
	public String getMenuTitle() {
		return null;
	}

	@Override
	public void onEvent(IEvent event) {

	}

}
