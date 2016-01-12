package com.mysassa.core.website.templating;

import com.mysassa.core.website.model.Website;
import com.mysassa.core.hosting.service.HostingService;
import com.mysassa.core.media.model.Media;
import com.mysassa.core.media.services.MediaService;
import org.apache.wicket.request.IRequestCycle;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.util.file.Files;

import java.io.File;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Loads a Template give a Request.
 *
 * <p>
 *     This is the Core of templating, When the RequestMapper recognizes the Domain as a Templated site,
 *     this class handles requests that point to a templated site.
 * </p>
 * <p>
 *     This handles both static JS/CSS as well as TemplatedFiles.
 * </p>
 */
public class MediaRequestHandler implements IRequestHandler {
	private final Request request;
	private final Website website;
	private final String uuid;
	private final Media media;
	int width = 0, height = 0;

	public MediaRequestHandler(Request request) {
		this.request = request;
		this.website = HostingService.get().findWebsite(request.getClientUrl());
		String[] parts = request.getClientUrl().getPath().split("/");
		if (parts.length >= 2) {
			uuid = parts[1];
			media = MediaService.get().findByUid(uuid);
			if (parts.length >= 4) {
				width = Integer.parseInt(parts[2]);
				height = Integer.parseInt(parts[3]);
			}
		} else {
			uuid = null;
			media = null;
		}

	}

	@Override
	public void respond(IRequestCycle requestCycle) {
		if (0 != height && width != 0) {
			respondScaled(requestCycle);
		} else {
			respondOriginal(requestCycle);
		}

	}

	private void respondOriginal(IRequestCycle requestCycle) {
		checkNotNull(media);
		try {
			WebResponse response = ((WebResponse) requestCycle.getResponse());
			response.setStatus(200);
			File file = media.calculateOriginalFile();
			final byte[] data = Files.readBytes(file);
			response.setContentLength(data.length);
			response.setHeader("cache-control", "public, max-age=360000, cache");
			response.write(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void respondScaled(IRequestCycle requestCycle) {
		try {
			WebResponse response = ((WebResponse) requestCycle.getResponse());
			response.setStatus(200);
			File file = media.calculateScaledFile(width, height);
			final byte[] data = Files.readBytes(file);
			response.setContentLength(data.length);
			response.setHeader("cache-control", "public, max-age=360000, cache");
			response.write(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void detach(IRequestCycle requestCycle) {

	}
}
