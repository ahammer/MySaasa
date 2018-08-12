package com.mysaasa;

import com.google.common.eventbus.EventBus;
import org.h2.tools.Server;
import org.slf4j.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.sql.SQLException;

/**
 * We handle the life span of the servlet,
 *
 * which is the startup/shutdown of the internal DB as well. Created by Adam on 3/23/14.
 */
public class SimpleServletContextListener implements ServletContextListener {
	private final Logger logger = org.slf4j.LoggerFactory.getLogger(SimpleServletContextListener.class);
	Server h2Server;

	public SimpleServletContextListener() {

	}

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		try {
			h2Server = Server.createTcpServer().start();
		} catch (SQLException e) {
			throw new RuntimeException("Can not start H2 Database", e);
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent) {
		logger.info("Shutting down H2 Databasse");
		h2Server.stop();
	}
}
