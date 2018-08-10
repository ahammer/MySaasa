package com.mysaasa;

import org.apache.wicket.protocol.http.ContextParamWebApplicationFactory;
import org.apache.wicket.protocol.http.WicketFilter;
import org.apache.wicket.protocol.http.WicketServlet;
import org.apache.wicket.protocol.ws.javax.MyEndpointConfig;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.websocket.jsr356.server.ServerContainer;
import org.eclipse.jetty.websocket.jsr356.server.deploy.WebSocketServerContainerInitializer;

import javax.servlet.ServletException;
import javax.websocket.DeploymentException;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MySaasaServer {
	private Server server;
	private final Logger logger = Logger.getLogger(MySaasaServer.class.getSimpleName());

	public void start() throws Exception {
		if (server != null) {
			return;
		}

		server = new Server();
		ServletHolder servletHolder = new ServletHolder(WicketServlet.class);
		servletHolder.setInitParameter(ContextParamWebApplicationFactory.APP_CLASS_PARAM, MySaasa.class.getName());
		servletHolder.setInitParameter(WicketFilter.FILTER_MAPPING_PARAM, "/*");
		ServletContextHandler servletContextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
		servletContextHandler.addServlet(servletHolder, "/*");
		server.setHandler(servletContextHandler);
		HttpConfiguration http_config = getHttpConfig();
		ServerConnector httpsConnector = initializeHttpsConnector(http_config);
		ServerConnector httpConnector = initializeHttpConnector(http_config);
		applyConnectors(httpsConnector, httpConnector);

		server.start();

		try {
			InputStream stream = new URL("http://localhost:" + DefaultPreferences.getPort()).openConnection().getInputStream();
			stream.read();
		} catch (Exception e) {
			//We hit localhost just to launch the servlet
		}
	}

	private void setupWebSocketContext(WebAppContext bb) throws ServletException, DeploymentException {
		ServerContainer wscontainer = WebSocketServerContainerInitializer.configureContext(bb);
		wscontainer.addEndpoint(new MyEndpointConfig());
	}

	private void applyConnectors(ServerConnector httpsConnector, ServerConnector httpConnector) {
		if (httpsConnector != null) {
			//Http + Https
			server.setConnectors(new Connector[]{httpConnector, httpsConnector});
		} else {
			//Http Only
			server.setConnectors(new Connector[]{httpConnector});
		}
	}

	private WebAppContext initializeWebAppContent() {
		WebAppContext bb = new WebAppContext();
		bb.setServer(server);
		//bb.setContextPath("./");
		//bb.setResourceBase("./");
		//bb.setWar("./webapp");
		return bb;
	}

	private HttpConfiguration getHttpConfig() {
		HttpConfiguration http_config = new HttpConfiguration();
		http_config.setSecureScheme("https");
		http_config.setSecurePort(DefaultPreferences.getSecurePort());
		http_config.setSendXPoweredBy(true);
		http_config.setSendServerVersion(true);
		return http_config;
	}

	private ServerConnector initializeHttpConnector(HttpConfiguration http_config) {
		ServerConnector httpConnector = new ServerConnector(server, new HttpConnectionFactory(http_config));
		httpConnector.setPort(DefaultPreferences.getPort());
		httpConnector.setIdleTimeout(30000);
		return httpConnector;
	}

	private ServerConnector initializeHttpsConnector(HttpConfiguration http_config) {
		if (MySaasaDaemon.isLocalMode()) {
			logger.log(Level.INFO, "In local mode, no https");
			return null;
		}

		File keystoreFile = new File(DefaultPreferences.getConfigPath() + "/certificates/main.jks");
		ServerConnector https = null;
		if (keystoreFile.exists()) {
			logger.log(Level.INFO, "Keystore file exists, loading {0}", keystoreFile.getAbsolutePath());
			SslContextFactory sslContextFactory = new SslContextFactory();
			sslContextFactory.setKeyStorePath(keystoreFile.getAbsolutePath());
			sslContextFactory.setTrustStorePath(keystoreFile.getAbsolutePath());
			sslContextFactory.setKeyStorePassword(DefaultPreferences.getProperties().getProperty(DefaultPreferences.PREF_KEYSTORE_PASSWORD));
			sslContextFactory.setKeyManagerPassword(DefaultPreferences.getProperties().getProperty(DefaultPreferences.PREF_KEYSTORE_PASSWORD));

			for (String string: sslContextFactory.getAliases()) {
				logger.log(Level.INFO, "SSL ALIAS: {0}", string);
			}

			http_config.addCustomizer(new SecureRequestCustomizer());

			SslConnectionFactory sslConnectionFactory = new SslConnectionFactory(sslContextFactory, HttpVersion.HTTP_1_1.asString());
			HttpConnectionFactory httpConnectionFactory = new HttpConnectionFactory(http_config);
			https = new ServerConnector(server, sslConnectionFactory, httpConnectionFactory);

			https.setPort(DefaultPreferences.getSecurePort());
			https.setIdleTimeout(500000);
		}
		return https;
	}

	public void stop() throws Exception {
		server.stop();
		server = null;
	}
}
