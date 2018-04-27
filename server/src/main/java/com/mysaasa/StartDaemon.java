package com.mysaasa;

import org.apache.commons.daemon.Daemon;
import org.apache.commons.daemon.DaemonContext;

import java.util.Scanner;

/**
 * Manages the daemon
 *
 * Created by Adam on 10/12/2014.
 */
public class StartDaemon implements Daemon {

	private static final StartDaemon serverLauncher = new StartDaemon();

	/**
	 * The Java entry point.
	 * @param args Command line arguments, all ignored.
	 */
	public static void main(String[] args) {
		// the main routine is only here so I can also run the app from the command line
		serverLauncher.initialize();
		Scanner sc = new Scanner(System.in);
		// wait until receive stop command from keyboard
		System.out.printf("Enter 'stop' to halt: ");
		while (!sc.nextLine().toLowerCase().equals("stop"))
			;
		serverLauncher.terminate();
	}

	// Implementing the Daemon interface is not required for Windows but is for Linux
	@Override
	public void init(DaemonContext arg0) throws Exception {
		//log.debug("Daemon init");
	}

	@Override
	public void start() {
		initialize();
	}

	@Override
	public void stop() {
		terminate();
	}

	@Override
	public void destroy() {}

	private void initialize() {
		try {
			StartServer.main(new String[]{});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void terminate() {
		if (StartServer.server != null) {
			try {
				StartServer.server.stop();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
