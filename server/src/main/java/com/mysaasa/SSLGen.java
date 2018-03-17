package com.mysaasa;

import com.mysaasa.core.hosting.service.HostingService;

import com.mysaasa.core.website.model.Website;
import org.shredzone.acme4j.*;
import org.shredzone.acme4j.challenge.Http01Challenge;
import org.shredzone.acme4j.exception.AcmeConflictException;
import org.shredzone.acme4j.exception.AcmeException;
import org.shredzone.acme4j.util.CSRBuilder;
import org.shredzone.acme4j.util.CertificateUtils;
import org.shredzone.acme4j.util.KeyPairUtils;

import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Think of this class as a script.
 *
 * It attempts to get certificates and prepare a .jks file that
 * jetty can use to host SSL for MySaasa domains
 *
 *
 * Edit: Revise to be like this
 * 1) Create Keystore if none exists
 * 2) Iterate over the certificates and if they are valid
 * 3) If missing or old, get auth and add to keystore
 * 5) Notify SSL to regenerate
 */
public class SSLGen {

	static final String ROOT_KEY_URL = "https://letsencrypt.org/certs/isrgrootx1.pem.txt";
	static final String INTERMEDIATE_KEY_URL = "https://letsencrypt.org/certs/lets-encrypt-x3-cross-signed.pem.txt";
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
		//if (1==1) return;
		new Thread(() -> {
			System.out.println("Updating Certificate Process");

			try {
				loadKeyStore();
				downloadBasicCerts();
				loadApplicationCertificate();
				connectToLetsEncrypt();
				getCertsForSites();
				saveKeystore();
			} catch (Exception e) {
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
			//If not valid, or not existing, we return false
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

		return;
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
		String contactEmail = Simple.getContactEmail();
		checkNotNull(contactEmail, "contactEmail required in settings.properties to connect to lets encrypt");
		RegistrationBuilder builder = new RegistrationBuilder();
		builder.addContact("mailto:"+contactEmail);

		try {
			registration = builder.create(session);
		} catch (AcmeConflictException e) {
			registration = Registration.bind(session, e.getLocation());
		}
		registration.modify()
				.setAgreement(registration.getAgreement())
				.commit();

		URL accountLocationUrl = registration.getLocation();
		System.out.println("Connected Successfully to LetsEncrypt: " + accountLocationUrl.toString());

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
		String certificatePath = Simple.getConfigPath() + "certificates";
		new File(certificatePath).mkdir();

		String accountCertPath = Simple.getConfigPath() + "certificates/instance.pem";
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
				.filter(website -> !website.production.contains(".test"))
				.map(website -> website.production)
				.collect(Collectors.toList());
	}

	private void getCertsForSites() throws Exception {
		List<String> sites = getApplicableDomains();

		checkNotNull(registration, "Must be registered to do this");
		for (String site : sites) {
			if (!isDomainValid(site)) {
				System.out.println("Getting Certificate For: "+site);
				authorizeDomain(site);
				downloadCert(site);
			} else {
				System.out.println("Domain cert already valid: "+site);
			}
		}
	}

	private void downloadCert(String site) throws Exception {
		checkNotNull(registration, "Need a registration to do this");
		KeyPair domainKeyPair = KeyPairUtils.createKeyPair(2048);
		FileWriter fw = new FileWriter(getCertPath() + site + "-priv.pem");
		KeyPairUtils.writeKeyPair(domainKeyPair, fw);

		CSRBuilder csrb = new CSRBuilder();
		csrb.addDomain(site);
		csrb.setOrganization("MySaasa");
		csrb.sign(domainKeyPair);
		byte[] csr = csrb.getEncoded();

		fw = new FileWriter(getCertPath() + site + ".csr");
		csrb.write(fw);

		Certificate certificate = registration.requestCertificate(csr);
		X509Certificate cert = certificate.download();

		//Wipe and reload
		if (mainKeyStore.containsAlias(site)) {
			mainKeyStore.deleteEntry(site);
		}
		mainKeyStore.setKeyEntry(site, domainKeyPair.getPrivate(), getPasswordChars(), new java.security.cert.Certificate[]{cert});
	}

	private char[] getPasswordChars() {
		return "password".toCharArray();
	}

	private String getCertPath() {
		return Simple.getConfigPath() + "certificates/";
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
		while (!currentlyValid && count < 100) {

			System.out.println(count + "/" + 100);
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

	public static boolean hasActiveChallenge(String filename, Website website) {
		if (activeChallengeMap.containsKey(website.production)) {
			Http01Challenge challenge = activeChallengeMap.get(website.production);
			return (filename.equalsIgnoreCase(".well-known/acme-challenge/" + challenge.getToken()));
		}
		return false;
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
		saveKeystore();
	}

	private File getKeystoreFile() {
		return new File(getCertPath()+"main.jks");
	}

	private void saveKeystore() throws IOException, NoSuchAlgorithmException, CertificateException, KeyStoreException {
		File file = getKeystoreFile();
		mainKeyStore.store(new FileOutputStream(file), getPasswordChars());
	}
}
