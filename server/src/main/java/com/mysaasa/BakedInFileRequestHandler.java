package com.mysaasa;

import org.apache.commons.io.IOUtils;
import org.apache.wicket.request.IRequestCycle;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.util.file.Files;
import org.eclipse.jetty.util.security.Credential;
import sun.nio.ch.IOUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Logger;

public class BakedInFileRequestHandler implements IRequestHandler {
	private static final Logger logger = Logger.getLogger("BakedInFileRequestHandler");
	private final InputStream stream;

	public BakedInFileRequestHandler(Request request) {
		File tempFile;
		String path = request.getUrl().getPath();
		String resourcePath = path;
		stream = Simple.class.getClassLoader().getResourceAsStream(resourcePath);
	}

	public static boolean isValidRequest(Request request) {

		String path = request.getUrl().getPath();
		if (path.length() == 0)
			return false;

		File file = null;
		try {
			String resourcePath = path;
			InputStream stream = Simple.class.getClassLoader().getResourceAsStream(resourcePath);

			if (stream != null) {
				stream.close();
				return true;
			}

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return false;
	}

	@Override
	public void respond(IRequestCycle requestCycle) {
		final WebResponse response = (WebResponse) requestCycle.getResponse();
		try {
			final byte[] data = IOUtils.toByteArray(stream);
			response.setHeader("cache-control", "private, max-age=0, no-cache");
			response.setContentLength(data.length);
			response.write(data);
		} catch (Exception e) {
			errorResponseBackup(e, response);
		}
	}

	@Override
	public void detach(IRequestCycle requestCycle) {

	}

	private void errorResponseBackup(Exception e, WebResponse response) {
		final byte[] data = e.toString().getBytes();
		response.setHeader("cache-control", "private, max-age=0, no-cache");
		response.setContentLength(data.length);
		response.write(data);
		System.out.println("Error:::: " + e);
		e.printStackTrace();

	}
}
