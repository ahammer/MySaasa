package com.mysaasa;

import org.apache.wicket.protocol.ws.javax.MyEndpointConfig;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.websocket.jsr356.server.ServerContainer;
import org.eclipse.jetty.websocket.jsr356.server.deploy.WebSocketServerContainerInitializer;

import javax.servlet.ServletException;
import javax.websocket.DeploymentException;
import java.io.File;

public class MySaasaServer {
	private Server server;

	public void start() throws Exception {
		if (server != null) {
			return;
		}
		server = new Server();
		HttpConfiguration http_config = getHttpConfig();
		ServerConnector httpsConnector = initializeHttpsConnector(http_config);
		ServerConnector httpConnector = initializeHttpConnector(http_config);
		WebAppContext webAppContext = initializeWebAppContent();
		applyConnectors(httpsConnector, httpConnector);
		server.setHandler(webAppContext);
		setupWebSocketContext(webAppContext);
		server.start();
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
		bb.setContextPath("/");
		bb.setWar("./webapp");
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
		if (MySaasaDaemon.isLocalMode())
			return null;

		File keystoreFile = new File(DefaultPreferences.getConfigPath() + "/certificates/main.jks");
		ServerConnector https = null;
		if (keystoreFile.exists()) {
			SslContextFactory sslContextFactory = new SslContextFactory();
			sslContextFactory.setKeyStorePath(keystoreFile.getAbsolutePath());
			sslContextFactory.setTrustStorePath(keystoreFile.getAbsolutePath());
			sslContextFactory.setKeyStorePassword(DefaultPreferences.getProperties().getProperty(DefaultPreferences.PREF_KEYSTORE_PASSWORD));
			sslContextFactory.setKeyManagerPassword(DefaultPreferences.getProperties().getProperty(DefaultPreferences.PREF_KEYSTORE_PASSWORD));
			HttpConfiguration https_config = new HttpConfiguration(http_config);
			https_config.addCustomizer(new SecureRequestCustomizer());

			https = new ServerConnector(server,
					new SslConnectionFactory(sslContextFactory, HttpVersion.HTTP_1_1.asString()),
					new HttpConnectionFactory(https_config));

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
