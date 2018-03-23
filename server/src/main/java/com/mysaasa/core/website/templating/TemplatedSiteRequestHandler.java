package com.mysaasa.core.website.templating;

import java.io.*;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import com.mysaasa.SSLGen;
import com.mysaasa.SimpleImpl;
import com.mysaasa.MysaasaRequestMapper;
import com.mysaasa.core.website.model.Website;
import com.mysaasa.Simple;
import com.mysaasa.core.hosting.service.HostingService;
import com.mysaasa.core.security.services.SessionService;
import com.mysaasa.core.security.services.session.AdminSession;
import com.mysaasa.interfaces.templating.BlogTemplateService;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.wicket.Session;
import org.apache.wicket.request.*;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.util.file.Files;
import org.apache.wicket.util.string.Strings;

import org.eclipse.jetty.util.security.Credential;
import org.shredzone.acme4j.challenge.Http01Challenge;

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
public class TemplatedSiteRequestHandler implements IRequestHandler {
	public final static String CLIENT_URL_INTEGRATION = "__media/";
	private final String contentType;
	private final String encoding;
	private final String filename;
	private final Website website;
	private final File file;
	private final Map<Website, VelocityEngine> engines = new HashMap<>();

	private final VelocityContext context = new VelocityContext();
	private final String path;

	/**
	 * Checks to see if this is a ValidRequest for this handler
	 * @param request
	 * @return
	 */
	public static boolean IsValidRequest(final Request request) {
		HostingService service = HostingService.get();
		String host = request.getUrl().getHost();
		String session_part = "";

		if (HostingService.isSessionLinked(host)) {
			String real_domain = HostingService.RealDomain(host);
			session_part = HostingService.Session(host);
			host = real_domain;
		}

		String filename = getRequestedFile(request);
		Website website = service.findWebsite(request.getUrl());
		Website theme = null;

		//TODO look up admin session via Session Service and URL
		if (SessionService.get().hasAdminSession(session_part)) {
			AdminSession adminSession = AdminSession.get();
			if (adminSession.getTheme() != null) {
				theme = adminSession.getTheme();
			}
		}

		if (website == null)
			return false;

		//Check if there is a challenge for this domain
		Http01Challenge challenge = SSLGen.getActiveChallenge(filename, host);
		if (challenge != null) {
			return true;
		}

		Website selected = (theme != null) ? theme : website;

		if (website.getProduction().equals(host)
				|| HostingService.get().findDomain(host) != null) {
			File file = new File(selected.calculateWebsiteRootAsString() + filename);
			boolean exists = file.exists();
			return exists;

		} else if (website.getStaging().equals(host)) {
			File file = new File(selected.calculateStagingRoot() + "/" + filename);
			return file.exists();
		}
		return false;
	}

	Website theme;

	public TemplatedSiteRequestHandler(VelocityEngine mEngine, final Request request) {
		checkNotNull(mEngine);
		checkNotNull(context);
		String host = request.getUrl().getHost();
		String session_part = "";

		if (HostingService.isSessionLinked(host)) {
			int second_part = host.indexOf("_", 2);
			session_part = host.substring(2, second_part);
		}

		HostingService service = HostingService.get();
		encoding = "UTF-8";
		filename = getRequestedFile(request);
		website = service.findWebsite(request.getClientUrl());
		theme = (SessionService.get().hasAdminSession(session_part)) ? AdminSession.get().getTheme() : null;

		path = request.getClientUrl().getPath();
		if (request.getClientUrl().getHost().equals(website.getStaging())) {
			file = new File(((theme != null) ? theme : website).calculateStagingRoot() + "/" + filename);
		} else {
			file = new File(((theme != null) ? theme : website).calculateWebsiteRootAsString() + filename);
		}

		contentType = lookupContentType();
	}

	/**
	 * @param requestCycle the current Request Cycle from Wicket
	 * @return the configured encoding or the request's one as default
	 */
	private String getEncoding(final IRequestCycle requestCycle) {
		String encoding = this.encoding;
		if (Strings.isEmpty(encoding)) {
			final Charset charset = requestCycle.getRequest().getCharset();
			if (charset != null) {
				encoding = charset.name();
			}
		}
		return encoding;
	}

	private static String getRequestedFile(Request request) {
		if (request.getClientUrl().getPath().equals(""))
			return "index.html";
		return request.getClientUrl().getPath();
	}

	/**
	
	 *
	 * @see org.apache.wicket.request.IRequestHandler#respond(org.apache.wicket.request.IRequestCycle)
	 */
	@Override
	public void respond(final IRequestCycle requestCycle) {
		final String encoding = getEncoding(requestCycle);
		final WebResponse response = (WebResponse) requestCycle.getResponse();
		AdminSession prefs = getAdminSession(requestCycle);

		response.setContentType(contentType + ";charset=" + encoding);
		try {
			if (contentType.equals("text/html")) {
				try {
					templatedResponse(response, prefs);
				} catch (Exception e) {
					e.printStackTrace();
					errorResponseBackup(e, response);
				}
			} else {
				rawResponse(response);
			}
		} catch (final FileNotFoundException | ResourceNotFoundException e) {

			//Maybe this is a challenge?
			Http01Challenge challenge = SSLGen.getActiveChallenge(path, requestCycle.getRequest().getUrl().getHost());
			if (challenge != null) {
				stringResponse(response, challenge.getAuthorization());
				return;
			}
			_404Response(response);
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Does a raw response, but appends the error to the end of the file
	 *
	 * @param e
	 * @param response
	 * @throws IOException
	 */
	private void errorResponseBackup(Exception e, WebResponse response) throws IOException {
		final byte[] data = e.toString().getBytes();
		response.setHeader("cache-control", "private, max-age=0, no-cache");
		response.setHeader("ETag", Credential.MD5.digest(file.lastModified() + file.getAbsolutePath()));
		response.setContentLength(data.length);
		response.write(data);
		System.out.println("Error:::: " + e);
		e.printStackTrace();

	}

	private void _404Response(WebResponse response) {

		final byte[] data = ("404 Could not load " + filename).getBytes();
		response.setStatus(404);
		response.setContentLength(data.length);
		try {
			response.write(data);
		} catch (Exception e) {

		}
	}

	private void stringResponse(WebResponse response, String s) {
		final byte[] data = s.getBytes();
		response.setStatus(200);
		response.setContentLength(data.length);
		try {
			response.write(data);
		} catch (Exception e) {

		}
	}

	private void rawResponse(WebResponse response) throws IOException {
		final byte[] data = Files.readBytes(file);
		response.setHeader("cache-control", "private, max-age=0, no-cache");
		response.setHeader("ETag", Credential.MD5.digest(file.lastModified() + file.getAbsolutePath()));
		response.setContentLength(data.length);
		response.write(data);
	}

	private void templatedResponse(WebResponse response, AdminSession prefs) {
		response.setHeader("cache-control", "private, max-age=0, no-cache");
		//org.apache.wicket.protocol.ws.javax.WicketServerApplicationConfig

		VelocityEngine engine = getEngine();
		synchronized (engine) {
			boolean debugMode = (prefs == null) ? false : prefs.getEditMode();
			try {
				TemplateHelperService templateHelperService = SimpleImpl.get().getInjector().getProvider(TemplateHelperService.class).get();
				engine.setProperty("resource.loader", "file");
				engine.setProperty("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.FileResourceLoader");
				engine.setProperty("file.resource.loader.cache", "false");
				engine.setProperty("file.resource.loader.modificationCheckInterval", "2");

				Request request = RequestCycle.get().getRequest();
				if (prefs != null && prefs.getTheme() != null) {
					engine.setProperty("file.resource.loader.path", prefs.getTheme().calculateWebsiteRootAsString());
				} else {
					if (request.getClientUrl().getHost().equals(website.getStaging())) {
						engine.setProperty("file.resource.loader.path", website.calculateStagingRoot().getAbsolutePath());
					} else {
						engine.setProperty("file.resource.loader.path", website.calculateWebsiteRootAsString());
					}
				}
				engine.init();

				final Template template = engine.getTemplate(filename);
				final StringWriter writer = new StringWriter();

				bindTemplateServices(debugMode, templateHelperService);

				//TODO document this in WIKI
				context.put("get", new QueryParamProxy(request.getQueryParameters()));
				context.put("post", new QueryParamProxy(request.getPostParameters()));
				context.put("self", request.getClientUrl().toString());
				context.put("file", request.getClientUrl().getProtocol() + "://" + request.getClientUrl().getHost() + ":" + request.getClientUrl().getPort() + "/" + request.getClientUrl().getPath());
				context.put("baseDomain", Simple.getCurrentDomain());
				context.put("port", Simple.getPort());

				template.merge(context, writer);
				//Do the merge here (Put the Developer stuff in)
				if (debugMode) {
					int port = SimpleImpl.getPort();
					//Used in inline editor
					String ckEditor = "<script type=\"text/javascript\" src=\"/ckeditor/ckeditor.js\"></script>\n";
					String jquery = "<script type=\"text/javascript\" src=\"/jquery-ui-1.10.3.custom/js/jquery-1.9.1.js\"></script>\n";
					String json2 = "<script type=\"text/javascript\" src=\"/json2/json2.js\"></script>\n";

					//Notifies the admin with the current document.URL
					String checkIframeScript = "if (window.self == window.top) {\n" + "if (window.location.indexOf(\"?\") > -1) {" + "window.location=window.location+'&" + MysaasaRequestMapper.CANCEL_SESSION_LINK + "'" + "} else {" + "window.location=window.location+'?&" + MysaasaRequestMapper.CANCEL_SESSION_LINK + "'" + "}" + "}" + "\n";

					String notifyAdminScript = "window.parent.postMessage(" + "{" + "   title:''," + "   subtitle:''," + "   categories:''," + "   method:'load'," + "   content:''," + "   summary:''," + "   id:document.URL" + "},'*');";

					String script = "\n" + ckEditor + jquery + json2 + "\n" + "<script type=\"text/javascript\">" + BlogTemplateService.MAKE_WYSIWYG_CODE + "\n" + notifyAdminScript + "\n" + checkIframeScript +

					"</script>" + "<style type=\"text/css\">" + BlogTemplateService.MAKE_WYSIWYG_CSS + "</style>";

					StringBuilder builder = new StringBuilder(writer.toString());
					int index = builder.indexOf("</head>");
					if (index == -1)
						index = builder.indexOf("</HEAD>");
					if (index == -1)
						index = builder.indexOf("</Head>");
					if (index != -1) {
						builder.insert(index, script);

						final byte[] data = builder.toString().getBytes();
						response.setContentLength(data.length);
						response.write(data);

						templateHelperService.clearRequestProperties();
						return;

					}
				}
				//------------------
				final byte[] data = writer.toString().getBytes();
				response.setContentLength(data.length);
				response.write(data);
				templateHelperService.clearRequestProperties();
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			} catch (OutOfMemoryError e) {
				throw (e);
			}
		}

	}

	private void bindTemplateServices(boolean debugMode, TemplateHelperService templateHelperService) {
		templateHelperService.setRequestProperties(debugMode, website);
		Map<String, Class> services = templateHelperService.getServiceMap();
		for (String key : services.keySet()) {
			context.put(key, SimpleImpl.get().getInjector().getProvider(services.get(key)).get());
		}
	}

	/**
	 * Get's the Admin session used by the OTHER Session, the one from the Parent iframe
	 *
	 * @param requestCycle
	 * @return
	 */
	private AdminSession getAdminSession(final IRequestCycle requestCycle) {
		return SessionService.get().getAdminSession(Session.get());
	}

	private String lookupContentType() {
		String contentType = URLConnection.guessContentTypeFromName(file.getName());
		if (file.getName().endsWith("css")) {
			contentType = "text/css";
		} else if (file.getName().endsWith("js")) {
			contentType = "text/javascript";
		} else if (file.getName().endsWith("ico")) {
			contentType = "image/x-icon";
		} else if (file.getName().endsWith("svg")) {
			contentType = "image/svg+xml";
		} else if (contentType == null) {
			contentType = "text/undefined";
		}
		return contentType;
	}

	@Override
	public void detach(IRequestCycle requestCycle) {}

	public VelocityEngine getEngine() {
		VelocityEngine engine = engines.get(website);
		if (engine == null) {
			engine = new VelocityEngine();
			engines.put(website, engine);
		}
		return engine;
	}
}
