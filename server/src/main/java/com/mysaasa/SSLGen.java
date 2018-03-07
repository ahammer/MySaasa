package com.mysaasa;

import com.mysaasa.core.hosting.service.HostingService;

import com.mysaasa.core.website.model.Website;
import com.mysaasa.messages.data.Bundle;
import org.apache.commons.collections4.map.UnmodifiableMap;
import org.shredzone.acme4j.Authorization;
import org.shredzone.acme4j.Registration;
import org.shredzone.acme4j.RegistrationBuilder;
import org.shredzone.acme4j.Session;
import org.shredzone.acme4j.challenge.Challenge;
import org.shredzone.acme4j.challenge.Http01Challenge;
import org.shredzone.acme4j.exception.AcmeConflictException;
import org.shredzone.acme4j.exception.AcmeException;
import org.shredzone.acme4j.util.KeyPairUtils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.security.KeyPair;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;

public class SSLGen {

	private KeyPair applicationKeyPair;
	private Session session;
	private Registration registration;

	public static Map<String, Http01Challenge> activeChallengeMap = new ConcurrentHashMap();

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


		try {
			registration = builder.create(session);
		} catch (AcmeConflictException e) {
			registration = Registration.bind(session, e.getLocation());
		}
		registration.modify()
				.setAgreement(registration.getAgreement())
				.commit();

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

	private void getCertsForSites(List<String> sites) throws AcmeException {
		checkNotNull(registration, "Must be registered to do this");
		for (String site : sites) {
			authorizeDomain(site);
		}


	}

	private void authorizeDomain(String s) throws AcmeException {
		Authorization auth = registration.authorizeDomain(s);
		Http01Challenge challenge = auth.findChallenge(Http01Challenge.TYPE);
		activeChallengeMap.put(s, challenge);
		challenge.trigger();

	}

	public static boolean hasActiveChallenge(String filename, Website website) {
		if (activeChallengeMap.containsKey(website.production)) {
			Http01Challenge challenge = activeChallengeMap.get(website.production);
			return (filename.equalsIgnoreCase(".well-known/acme-challenge/"+challenge.getToken()));
		}
		return false;
	}

	public static String getAuthorization(Website website) {
		if (!activeChallengeMap.containsKey(website.production)) {
			return "";
		}
		return activeChallengeMap.get(website.production).getAuthorization();
	}
}
