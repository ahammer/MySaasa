package com.mysaasa;

import com.mysaasa.core.hosting.service.HostingService;

import com.mysaasa.core.website.model.Domain;

import org.shredzone.acme4j.*;
import org.shredzone.acme4j.Certificate;
import org.shredzone.acme4j.challenge.Http01Challenge;
import org.shredzone.acme4j.exception.AcmeConflictException;
import org.shredzone.acme4j.exception.AcmeException;
import org.shredzone.acme4j.exception.AcmeRateLimitExceededException;
import org.shredzone.acme4j.util.CSRBuilder;
import org.shredzone.acme4j.util.KeyPairUtils;

import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Think of this class as a script.
 *
 * It attempts to getInstance certificates and prepare a .jks file that
 * jetty can use to host SSL for MySaasa domains
 *
 *
 * Edit: Revise to be like this
 * 1) Create Keystore if none exists
 * 2) Iterate over the certificates and if they are valid
 * 3) If missing or old, getInstance auth and add to keystore
 * 5) Notify SSL to regenerate
 */
public class SSLGen {
	private final Logger logger = Logger.getLogger("SSLGen");
	private static final String ROOT_KEY_URL = "https://letsencrypt.org/certs/isrgrootx1.pem.txt";
	private static final String INTERMEDIATE_KEY_URL = "https://letsencrypt.org/certs/letsencryptauthorityx3.pem.txt";
	private static final int CERTIFICATE_LOOK_AHEAD_TIME_MS = 1000 * 60 * 60 * 24 * 14;
	private static final String LETS_ENCRYPT_URL = "acme://letsencrypt.org/staging";
	private KeyPair applicationKeyPair;
	private Registration registration;
	private static Map<String, Http01Challenge> activeChallengeMap = new ConcurrentHashMap<>();
	private KeyStore mainKeyStore;

	SSLGen() {}

	/**
	 * Do the SSL stuff
	 * Create account with LetsEncrypt
	 * - Generate Certificates
	 * - Generate Account
	 * Request Appropriate Challenges
	 * -
	 */
	void doSSLMagic() {
		if (MySaasaDaemon.isLocalMode())
			return;

		new Thread(() -> {
			logger.info("Updating Certificate Process");

			try {
				loadKeyStore();
				downloadBasicCerts();
				loadApplicationCertificate();
				connectToLetsEncrypt();
				getCertsForSites();
				saveMainKeystore();
				logger.info("Certificate process complete");
			} catch (Exception e) {
				logger.log(Level.WARNING, "Certificate Process Uncaught Exception!!", e);
			}
		} ).start();

	}

	private boolean isDomainValid(String domain) {
		try {
			if (!mainKeyStore.containsAlias(domain))
				throw new IllegalStateException("Cert not in keystore for " + domain);
			X509Certificate cert = (X509Certificate) mainKeyStore.getCertificate(domain);
			cert.checkValidity(new Date(System.currentTimeMillis() + CERTIFICATE_LOOK_AHEAD_TIME_MS));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * We will need the root/intermediate certs for the keychain
	 */
	private void downloadBasicCerts() throws IOException {
		downloadFile(ROOT_KEY_URL, "root.pem");
		downloadFile(INTERMEDIATE_KEY_URL, "intermediate.pem");
	}

	private void downloadFile(String url, String file) throws IOException {
		URL website = new URL(url);
		try (ReadableByteChannel rbc = Channels.newChannel(website.openStream())) {
			try (FileOutputStream fos = new FileOutputStream(getCertPath() + file)) {
				fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
			}
		}
	}

	private void connectToLetsEncrypt() throws AcmeException {
		checkNotNull(applicationKeyPair, "We couldn't load an application key pair");
		Session session = new Session(getAcmeUrl(), applicationKeyPair);
		String contactEmail = DefaultPreferences.getContactEmail();
		checkNotNull(contactEmail, "contactEmail required in settings.properties to connect to lets encrypt");
		RegistrationBuilder builder = new RegistrationBuilder();
		builder.addContact("mailto:" + contactEmail);

		try {
			registration = builder.create(session);
		} catch (AcmeConflictException e) {
			registration = Registration.bind(session, e.getLocation());
		}
		registration.modify()
				.setAgreement(registration.getAgreement())
				.commit();

	}

	private String getAcmeUrl() {
		return LETS_ENCRYPT_URL;
	}

	/**
	 * Check if we have a
	 * /opt/mysaasa/certificates/instance.? <- Account Certificate
	 * /opt/mysaasa/certificates/domain/certificate.?
	 */
	private void loadApplicationCertificate() throws IOException {
		String certificatePath = getCertPath();
		File certPathFile = new File(certificatePath);
		if (!certPathFile.exists()) {
			boolean result = certPathFile.mkdir();
			if (!result)
				throw new IOException("Could not make certificate path");
		}

		String accountCertPath = getCertPath() + "instance-private-key.pem";
		File accountCertFile = new File(accountCertPath);
		if (!accountCertFile.exists()) {
			applicationKeyPair = KeyPairUtils.createKeyPair(2048);
			FileWriter fw = new FileWriter(accountCertPath);
			KeyPairUtils.writeKeyPair(applicationKeyPair, fw);
		} else {
			applicationKeyPair = KeyPairUtils.readKeyPair(new FileReader(accountCertFile));
		}

		logger.log(Level.INFO, "Loaded a key pair {0}", applicationKeyPair.getPublic());
	}

	private List<String> getApplicableDomains() {
		return HostingService
				.get()
				.getWebsites()
				.stream()
				.filter(website -> {
					if (website == null)
						return false;
					if (website.production == null)
						return false;
					if (website.organization == null)
						return false;
					if (website.organization.enabled == null)
						return false;
					if (!website.organization.enabled)
						return false;
					return !website.production.contains(".test");
				} )
				.flatMap(website -> {
					ArrayList<String> activeDomains = new ArrayList<>();
					if (website.production != null)
						activeDomains.add(website.production);
					List<Domain> domains = website.getDomains();
					domains.forEach(domain -> activeDomains.add(domain.domain));
					return activeDomains.stream();
				} )
				.collect(Collectors.toList());
	}

	private void getCertsForSites() throws AcmeException, InterruptedException, KeyStoreException, IOException {
		List<String> sites = getApplicableDomains();
		checkNotNull(registration, "Must be registered to do this");

		for (String site : sites) {
			if (!isDomainValid(site)) {
				logger.log(Level.INFO, "Getting Certificate for {0}", site);
				authorizeDomain(site);
				downloadCert(site);
			} else {
				logger.log(Level.INFO, "Domain cert already valid: {0}", site);

			}
		}
	}

	private void downloadCert(String site) throws IOException, AcmeException, KeyStoreException {
		checkNotNull(registration, "Need a registration to do this");
		KeyPair domainKeyPair = KeyPairUtils.createKeyPair(2048);
		CSRBuilder csrBuilder = new CSRBuilder();
		csrBuilder.addDomain(site);
		csrBuilder.setOrganization("MySaasa");
		csrBuilder.sign(domainKeyPair);
		byte[] csr = csrBuilder.getEncoded();

		try {
			Certificate certificate = registration.requestCertificate(csr);
			X509Certificate[] chain = certificate.downloadChain();

			//Wipe and reload
			if (mainKeyStore.containsAlias(site)) {
				mainKeyStore.deleteEntry(site);
			}

			mainKeyStore.setKeyEntry(site, domainKeyPair.getPrivate(), getPasswordChars(), chain);
			logger.log(Level.INFO, "Added cert to keystore: {0}", site);
		} catch (AcmeRateLimitExceededException e) {
			logger.log(Level.INFO, "Rate Limit Exceeded {0}", site);

			//Skip this one
			//Rate Limit Exceeded
		}
	}

	private char[] getPasswordChars() {
		return DefaultPreferences.getKeystorePassword().toCharArray();
	}

	private String getCertPath() {
		return DefaultPreferences.getConfigPath() + "certificates/";
	}

	private void authorizeDomain(String domain) throws AcmeException, InterruptedException {
		Authorization auth = registration.authorizeDomain(domain);
		Http01Challenge challenge = auth.findChallenge(Http01Challenge.TYPE);
		activeChallengeMap.put(domain, challenge);
		challenge.trigger();

		long timeout = 500;
		int count = 0;


		logger.log(Level.INFO, "Waiting to authorize domain: {0}/.well-known/acme-challenge/{1}",  new Object[]{domain, challenge.getToken()});
		while (count < 20) {
			logger.log(Level.INFO, "{0}/20", count);
			challenge.update();

			if (challenge.getStatus() == Status.VALID) {
				logger.log(Level.INFO, "Verified domain: {0}", domain);
				return;
			}


			Thread.sleep(timeout);
			count++;
			timeout *= 2;
			if (timeout > 10000)
				timeout = 10000;

		}

		logger.log(Level.INFO, "Could not verify domain: {0}", domain);

	}

	public static Http01Challenge getActiveChallenge(String domain) {
		Http01Challenge challenge = null;

		if (activeChallengeMap.containsKey(domain)) {
			challenge = activeChallengeMap.get(domain);
		}
		return challenge;
	}

	private void loadKeyStore() throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException {
		File file = getKeystoreFile();
		mainKeyStore = KeyStore.getInstance("JKS");

		if (!file.exists()) {
			mainKeyStore.load(null, null);
		} else {
			try (FileInputStream stream = new FileInputStream(getKeystoreFile())) {
				mainKeyStore.load(stream, getPasswordChars());
			}
		}
		saveMainKeystore();
	}

	private File getKeystoreFile() {
		return new File(getCertPath() + "main.jks");
	}

	private void saveMainKeystore() throws IOException, NoSuchAlgorithmException, CertificateException, KeyStoreException {
		logger.info("Saving Keystore");
		File file = getKeystoreFile();
		try (FileOutputStream stream = new FileOutputStream(file)) {
			mainKeyStore.store(stream, getPasswordChars());
		}
	}
}
