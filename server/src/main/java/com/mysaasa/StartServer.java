package com.mysaasa;

import org.apache.wicket.protocol.ws.javax.MyEndpointConfig;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.websocket.jsr356.server.ServerContainer;
import org.eclipse.jetty.websocket.jsr356.server.deploy.WebSocketServerContainerInitializer;

import java.io.File;

public class StartServer {
	public static Server server;

	public static void main(String[] args) throws Exception {

		String keystorePath = Simple.getConfigPath() + "/keystore.jks";
		File keystoreFile = new File(keystorePath);

		HttpConfiguration http_config = new HttpConfiguration();
		http_config.setSecureScheme("https");
		http_config.setSecurePort(Simple.getSecurePort());
		http_config.setSendXPoweredBy(true);
		http_config.setSendServerVersion(true);

		server = new Server();

		ServerConnector https = null;
		if (keystoreFile.exists()) {
			SslContextFactory sslContextFactory = new SslContextFactory();

			sslContextFactory.setKeyStorePath(keystoreFile.getAbsolutePath());
			sslContextFactory.setTrustStorePath(keystoreFile.getAbsolutePath());
			sslContextFactory.setKeyStorePassword(Simple.getProperties().getProperty(Simple.PREF_KEYSTORE_PASSWORD));
			sslContextFactory.setKeyManagerPassword(Simple.getProperties().getProperty(Simple.PREF_KEYSTORE_PASSWORD));
			HttpConfiguration https_config = new HttpConfiguration(http_config);
			https_config.addCustomizer(new SecureRequestCustomizer());


			https = new ServerConnector(server,
					new SslConnectionFactory(sslContextFactory, HttpVersion.HTTP_1_1.asString()),
					new HttpConnectionFactory(https_config));

			https.setPort(Simple.getSecurePort());
			https.setIdleTimeout(500000);
		}

		WebAppContext bb = new WebAppContext();
		bb.setServer(server);
		bb.setContextPath("/");
		bb.setWar("src/main/webapp");
		server.setHandler(bb);

		ServerConnector http = new ServerConnector(server, new HttpConnectionFactory(http_config));
		http.setPort(Simple.getPort());
		http.setIdleTimeout(30000);

		// Here you see the server having multiple connectors registered with
		// it, now requests can flow into the server from both http and https
		// urls to their respective ports and be processed accordingly by jetty.
		// A simple handler is also registered with the server so the example
		// has something to pass requests off to.

		// Set the connectors

		if (https != null) {
			server.setConnectors(new Connector[]{http, https});
		} else {
			server.setConnectors(new Connector[]{http});
		}

		ServerContainer wscontainer = WebSocketServerContainerInitializer.configureContext(bb);

		wscontainer.addEndpoint(new MyEndpointConfig());

		try {
			server.start();
		} catch (Exception e) {
			System.out.println(Simple.getPort());
			e.printStackTrace();
			System.exit(1);
		}

	}

}
