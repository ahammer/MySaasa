package com.mysaasa.core.media.panels;

import com.mysaasa.MySaasa;
import com.mysaasa.core.media.model.Media;
import com.mysaasa.core.media.services.MediaService;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import java.util.Iterator;
import java.util.List;

public class MediaDataProvider extends SortableDataProvider<Media, String> {

	@Override
	public Iterator<? extends Media> iterator(long first, long count) {
		MediaService service = MySaasa.getInstance().getInjector().getProvider(MediaService.class).get();
		List l = service.getMedia();
		return l.subList((int) first, (int) (first + count)).iterator();
	}

	@Override
	public IModel<Media> model(Media object) {
		return new CompoundPropertyModel(object);
	}

	@Override
	public long size() {
		MediaService service = MySaasa.getInstance().getInjector().getProvider(MediaService.class).get();
		List l = service.getMedia();
		return l.size();
	}
}
