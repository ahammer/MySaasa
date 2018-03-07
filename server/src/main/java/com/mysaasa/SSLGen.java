package com.mysaasa;

import com.mysaasa.core.hosting.service.HostingService;
import com.stripe.model.Account;
import org.shredzone.acme4j.Registration;
import org.shredzone.acme4j.RegistrationBuilder;
import org.shredzone.acme4j.Session;
import org.shredzone.acme4j.exception.AcmeConflictException;
import org.shredzone.acme4j.exception.AcmeException;
import org.shredzone.acme4j.util.KeyPairUtils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.security.KeyPair;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;

public class SSLGen {

	private KeyPair applicationKeyPair;
	private Session session;

	public SSLGen() {}

	/**
	 * Do the SSL stuff
	 * Create account with LetsEncrypt
	 * - Generate Certificates
	 * - Generate Account
	 * Request Appropriate Challenges
	 * -
	 */
	public void doSSLMagic() {
		System.out.println("Updating Certificate Process");
		List<String> sites = getApplicableDomains();

		//We need to generate certs for this instance
		try {
			loadApplicationCertificate();

			connectToLetsEncrypt();
			getCertsForSites(sites);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void connectToLetsEncrypt() throws AcmeException {
		checkNotNull(applicationKeyPair, "We couldn't load an application key pair");
		session = new Session("https://acme-staging.api.letsencrypt.org/directory", applicationKeyPair);
		String contactEmail = Simple.getContactEmail();
		checkNotNull(contactEmail, "contactEmail required in settings.properties to connect to lets encrypt");
		RegistrationBuilder builder = new RegistrationBuilder();
		builder.addContact("mailto:acme@example.com");

		Registration registration = null;
		try {
			registration = builder.create(session);
		} catch (AcmeConflictException e) {
			registration = Registration.bind(session, e.getLocation());
		}
		URL accountLocationUrl = registration.getLocation();
		System.out.println("Connected Successfully to LetsEncrypt: "+accountLocationUrl.toString());

	}

	/**
	 * Check if we have a
	 * /opt/mysaasa/certificates/instance.? <- Account Certificate
	 * /opt/mysaasa/certificates/domain/certificate.?
	 */
	private void loadApplicationCertificate() throws IOException {
		String certificatePath = Simple.getConfigPath()+"certificates";
		new File(certificatePath).mkdir();

		String accountCertPath = Simple.getConfigPath() + "certificates/instance.pem";
		File accountCertFile = new File(accountCertPath);
		if (!accountCertFile.exists()) {
			applicationKeyPair = KeyPairUtils.createKeyPair(2048);
			try (FileWriter fw = new FileWriter(accountCertPath)) {
				KeyPairUtils.writeKeyPair(applicationKeyPair, fw);
			}
		} else {
			applicationKeyPair = KeyPairUtils.readKeyPair(new FileReader(accountCertFile));
		}

		System.out.println("Loaded a keypair: "+applicationKeyPair.getPublic());
	}

	private List<String> getApplicableDomains() {
		return HostingService
				.get()
				.getWebsites()
				.stream()
				.filter(website -> !website.production.contains(".test"))
				.map(website -> website.production)
				.collect(Collectors.toList());
	}

	private void getCertsForSites(List<String> sites) {

	}

}
