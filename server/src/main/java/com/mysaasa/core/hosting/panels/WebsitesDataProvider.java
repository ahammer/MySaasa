package com.mysaasa.core.hosting.panels;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.google.common.collect.Iterators;
import com.mysaasa.core.security.services.SessionService;
import org.apache.wicket.Session;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import com.mysaasa.core.website.model.Website;

public class WebsitesDataProvider extends SortableDataProvider<Website, String> {
	private static final long serialVersionUID = 1L;

	public WebsitesDataProvider() {}

	@Override
	public Iterator<? extends Website> iterator(long first, long count) {
		List<Website> list = SessionService.get().getSecurityContext(Session.get()).getWebsites();

		//Preconditions
		if (first < 0 || count < 0 || count == 0)
			throw new IllegalArgumentException("Failed (first < 0 || count < 0 || count == 0) (first=" + first + " count=" + count + ")");

		//Edge Cases
		if (list.size() == 0)
			return Collections.EMPTY_LIST.iterator();

		//Clipping
		if (list.size() < first + count)
			return list.subList((int) first, list.size()).iterator();

		return list.subList((int) first, (int) (first + count)).iterator();
	}

	@Override
	public IModel<Website> model(Website object) {
		return new CompoundPropertyModel(object);
	}

	@Override
	public long size() {
		return SessionService.get().getSecurityContext(Session.get()).getWebsites().size();
	}
}
