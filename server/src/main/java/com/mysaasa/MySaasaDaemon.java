package com.mysaasa;

import org.apache.commons.daemon.Daemon;
import org.apache.commons.daemon.DaemonContext;

import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Manages the daemon
 *
 * Created by Adam on 10/12/2014.
 */
public class MySaasaDaemon implements Daemon {

	private static final MySaasaDaemon serverLauncher = new MySaasaDaemon();
	private MySaasaServer server;

	/**
	 * The Java entry point.
	 *
	 * @param args Command line arguments, all ignored.
	 */
	public static void main(String[] args) throws Exception {
		serverLauncher.start();
		Scanner sc = new Scanner(System.in);

		System.out.printf("Enter 'stop' to halt: ");
		boolean running = true;
		while (running) {
			try {
				if (sc.hasNext()) {
					String nextLine = sc.nextLine();
					if (nextLine.trim().equalsIgnoreCase("")) {
						//Do Nothing
					} else if (nextLine.toLowerCase().trim().equals("stop")) {
						serverLauncher.stop();
						running = false;
						return;
					} else {
						System.out.println("Command not recognized: " + nextLine);
					}
				} else {
					//Do Nothing
				}
			} catch (NoSuchElementException e) {
				e.printStackTrace();
			}
		}

	}

	// Implementing the Daemon interface is not required for Windows but is for Linux
	@Override
	public void init(DaemonContext arg0) throws Exception {
		//log.debug("Daemon init");
	}

	@Override
	public void start() throws Exception {
		server = new MySaasaServer();
		server.start();
	}

	@Override
	public void stop() throws Exception {
		server.stop();
		System.exit(0);
	}

	@Override
	public void destroy() {}

}
