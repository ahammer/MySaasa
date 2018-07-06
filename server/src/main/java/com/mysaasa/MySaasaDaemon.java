package com.mysaasa;

import com.mysaasa.core.hosting.service.HostingService;
import com.mysaasa.core.organization.model.Organization;
import com.mysaasa.core.organization.services.OrganizationService;
import com.mysaasa.core.users.model.User;
import com.mysaasa.core.users.service.UserService;
import com.mysaasa.core.website.model.Website;
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

	public static boolean isLocalMode() {
		return LOCAL_MODE;
	}

	private static boolean LOCAL_MODE = false;
	private MySaasaServer server;

	/**
	 * The Java entry point.
	 *
	 * @param args Command line arguments, all ignored.
	 */
	public static void main(String[] args) throws Exception {
		if (hasArgument("localmode", args)) {
			enableLocalMode();

			serverLauncher.start();

			while (!Simple.getInstance().isInitialized()) {
				//Wait for simple to initialize
				System.out.println("Waiting for init");
				Thread.sleep(50);
			}

			Organization o = new Organization();
			o.setName("Test Organization");
			o = OrganizationService.get().saveOrganization(o);
			User u = new User("admin", "admin", User.AccessLevel.ROOT);
			u.setOrganization(o);
			u = UserService.get().saveUser(u);

			Website website = new Website();
			website.setOrganization(u.getOrganization());
			website.setProduction("localhost");
			HostingService.get().saveWebsite(website);

			/*
			HostingService hostingService = HostingService.get();
			OrganizationService orgService = OrganizationService.get();
			Organization organization = new Organization();
			organization.setName("test");
			organization.setContactInfo(new ContactInfo());
			organization = organization.save();


			User test = new User("test", "test", User.AccessLevel.ROOT);
			test.setOrganization(organization);
			UserService.get().saveUser(test);
			*/



		} else {
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

	}

	private static void enableLocalMode() {

		System.out.println("Enabling Test/Localhost Mode");
		//Use in memory database
		MySaasaDaemon.LOCAL_MODE = true;
		Simple.IN_MEMORY_DATABASE = true;

		//Add localhost
		//Add default users
	}

	private static boolean hasArgument(String desired, String[] args) {
		for (String arg : args) {
			if (arg.equals(desired)) {
				return true;
			}
		}
		return false;
	}

	public static void stopNow() throws Exception {
		serverLauncher.stop();
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
		//System.exit(0);
	}

	@Override
	public void destroy() {}

}
