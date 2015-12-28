package com.mysassa.simple.core.media.panels;

import com.mysassa.simple.SimpleImpl;
import com.mysassa.simple.core.media.model.Media;
import com.mysassa.simple.core.media.services.MediaService;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import java.util.Iterator;
import java.util.List;

public class MediaDataProvider extends SortableDataProvider<Media, String> {

	@Override
	public Iterator<? extends Media> iterator(long first, long count) {
		MediaService service = SimpleImpl.get().getInjector().getProvider(MediaService.class).get();
		List l = service.getMedia();
		return l.subList((int) first, (int) (first + count)).iterator();
	}

	@Override
	public IModel<Media> model(Media object) {
		return new CompoundPropertyModel(object);
	}

	@Override
	public long size() {
		MediaService service = SimpleImpl.get().getInjector().getProvider(MediaService.class).get();
		List l = service.getMedia();
		return l.size();
	}
}
