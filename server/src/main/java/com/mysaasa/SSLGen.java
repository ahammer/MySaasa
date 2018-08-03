package com.mysaasa;

import com.mysaasa.core.hosting.service.HostingService;

import com.mysaasa.core.website.model.Domain;
import com.mysaasa.core.website.model.Website;
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

	static final String ROOT_KEY_URL = "https://letsencrypt.org/certs/isrgrootx1.pem.txt";
	static final String INTERMEDIATE_KEY_URL = "https://letsencrypt.org/certs/letsencryptauthorityx3.pem.txt";
	public static final int CERTIFICATE_LOOK_AHEAD_TIME_MS = 1000 * 60 * 60 * 24 * 14;
	public static final String LETS_ENCRYPT_URL = "acme://letsencrypt.org/";
	private KeyPair applicationKeyPair;
	private Session session;
	private Registration registration;

	public static Map<String, Http01Challenge> activeChallengeMap = new ConcurrentHashMap();
	private KeyStore mainKeyStore;

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
		if (MySaasaDaemon.isLocalMode())
			return;
		//if (1==1) return;
		new Thread(() -> {
			System.out.println("Updating Certificate Process");

			try {
				loadKeyStore();
				downloadBasicCerts();
				loadApplicationCertificate();
				connectToLetsEncrypt();
				getCertsForSites();
				saveMainKeystore();
				System.out.println("Certificate process complete");
			} catch (Exception e) {
				System.out.println("Certificate Process Uncaught Exception!!");
				e.printStackTrace();
			}
		} ).start();

	}

	private boolean isDomainValid(String domain) throws KeyStoreException, CertificateExpiredException, CertificateNotYetValidException {
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
	 *
	 * @throws IOException
	 */
	private void downloadBasicCerts() throws IOException {
		downloadFile(ROOT_KEY_URL, "root.pem");
		downloadFile(INTERMEDIATE_KEY_URL, "intermediate.pem");
	}

	private void downloadFile(String url, String file) throws IOException {
		URL website = new URL(url);
		ReadableByteChannel rbc = Channels.newChannel(website.openStream());
		FileOutputStream fos = new FileOutputStream(getCertPath() + file);
		fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
	}

	private void connectToLetsEncrypt() throws AcmeException {
		checkNotNull(applicationKeyPair, "We couldn't load an application key pair");
		session = new Session(getAcmeUrl(), applicationKeyPair);
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
		new File(certificatePath).mkdir();

		String accountCertPath = getCertPath() + "instance-private-key.pem";
		File accountCertFile = new File(accountCertPath);
		if (!accountCertFile.exists()) {
			applicationKeyPair = KeyPairUtils.createKeyPair(2048);
			FileWriter fw = new FileWriter(accountCertPath);
			KeyPairUtils.writeKeyPair(applicationKeyPair, fw);
		} else {
			applicationKeyPair = KeyPairUtils.readKeyPair(new FileReader(accountCertFile));
		}

		System.out.println("Loaded a keypair: " + applicationKeyPair.getPublic());
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

	private void getCertsForSites() throws Exception {
		List<String> sites = getApplicableDomains();

		checkNotNull(registration, "Must be registered to do this");
		for (String site : sites) {
			if (!isDomainValid(site)) {
				System.out.println("-------------------------------------------------------------");
				System.out.println("Getting Certificate For: " + site);
				authorizeDomain(site);
				downloadCert(site);
			} else {
				System.out.println("Domain cert already valid: " + site);
			}
		}
	}

	private void downloadCert(String site) throws Exception {
		checkNotNull(registration, "Need a registration to do this");
		KeyPair domainKeyPair = KeyPairUtils.createKeyPair(2048);
		CSRBuilder csrb = new CSRBuilder();
		csrb.addDomain(site);
		csrb.setOrganization("MySaasa");
		csrb.sign(domainKeyPair);
		byte[] csr = csrb.getEncoded();
		java.security.cert.Certificate root = loadCert("root.pem");
		java.security.cert.Certificate intermediate = loadCert("intermediate.pem");

		try {
			Certificate certificate = registration.requestCertificate(csr);
			X509Certificate cert = certificate.download();
			X509Certificate[] chain = certificate.downloadChain();

			//Wipe and reload
			if (mainKeyStore.containsAlias(site)) {
				mainKeyStore.deleteEntry(site);
			}
			mainKeyStore.setKeyEntry(site, domainKeyPair.getPrivate(), getPasswordChars(), chain);

			System.out.println("Added cert to keystore: " + site);
		} catch (AcmeRateLimitExceededException e) {
			System.out.println("Rate Limit Exceeded: " + site);
			//Skip this one
			//Rate Limit Exceeded
		}
	}

	private java.security.cert.Certificate loadCert(String file) throws FileNotFoundException, CertificateException {
		FileInputStream fis = new FileInputStream(getCertPath() + file);
		CertificateFactory cf = CertificateFactory.getInstance("X.509");
		return cf.generateCertificate(fis);
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
		boolean currentlyValid = false;
		System.out.println("Waiting to authorize domain: " + domain + "/.well-known/acme-challenge/" + challenge.getToken());
		while (!currentlyValid && count < 20) {
			System.out.println(count + "/" + 20);
			challenge.update();
			currentlyValid = challenge.getStatus() == Status.VALID;
			if (currentlyValid)
				break;
			Thread.sleep(timeout);
			count++;
			timeout *= 2;
			if (timeout > 10000)
				timeout = 10000;

		}

		if (currentlyValid) {
			System.out.println("Verified Domain: " + domain);
		} else {
			System.out.println("Could not verify domain: " + domain);
		}

	}

	public static Http01Challenge getActiveChallenge(String domain) {
		Http01Challenge challenge = null;

		if (activeChallengeMap.containsKey(domain)) {
			challenge = activeChallengeMap.get(domain);
		}
		return challenge;
	}

	public static String getAuthorization(Website website) {
		if (!activeChallengeMap.containsKey(website.production)) {
			return "";
		}
		return activeChallengeMap.get(website.production).getAuthorization();
	}

	public void loadKeyStore() throws Exception {
		File file = getKeystoreFile();
		mainKeyStore = KeyStore.getInstance("JKS");

		if (!file.exists()) {
			mainKeyStore.load(null, null);
		} else {
			mainKeyStore.load(new FileInputStream(getKeystoreFile()), getPasswordChars());
		}
		saveMainKeystore();
	}

	private File getKeystoreFile() {
		return new File(getCertPath() + "main.jks");
	}

	private void saveMainKeystore() throws IOException, NoSuchAlgorithmException, CertificateException, KeyStoreException {
		System.out.println("Saving Keystore");
		File file = getKeystoreFile();
		mainKeyStore.store(new FileOutputStream(file), getPasswordChars());
	}
}
