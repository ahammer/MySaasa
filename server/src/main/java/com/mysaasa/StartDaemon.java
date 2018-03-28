package com.mysaasa;

import org.apache.commons.daemon.Daemon;
import org.apache.commons.daemon.DaemonContext;

import java.util.Scanner;

/**
 * Manages the daemon getEditPanel, start/stop for embedding
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

	/**
	 * Static methods called by prunsrv to start/stop
	 * the Windows service.  Pass the argument "start"
	 * to start the service, and pass "stop" to
	 * stop the service.
	 *
	 * Taken lock, stock and barrel from Christopher Pierce's blog at http://blog.platinumsolutions.com/node/234
	 *
	 * @param args Arguments from prunsrv command line
	 **/
	public static void windowsService(String args[]) {
		String cmd = "start";
		if (args.length > 0) {
			cmd = args[0];
		}

		if ("start".equals(cmd)) {
			serverLauncher.windowsStart();
		} else {
			serverLauncher.windowsStop();
		}
	}

	public void windowsStart() {
		//log.debug("windowsStart called");
		initialize();
		while (StartServer.server != null && StartServer.server.isRunning()) {
			// don't return until stopped
			synchronized (this) {
				try {
					this.wait(60000); // wait 1 minute and check if stopped
				} catch (InterruptedException ie) {}
			}
		}
	}

	public void windowsStop() {
		//log.debug("windowsStop called");
		terminate();
		synchronized (this) {
			// stop the start loop
			this.notify();
		}
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
	public void destroy() {
		//log.debug("Daemon destroy");
	}

	/**
	 * Do the work of starting the engine
	 */
	private void initialize() {
		try {
			StartServer.main(new String[]{});
		} catch (Exception e) {
			e.printStackTrace();
		}
		/*
		if (engine == null) {
		    log.info("Starting the Engine");
		    ... spawn threads etc
		}
		*/
	}

	/**
	 * Cleanly stop the engine.
	 */
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
