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
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;


// -> Root
//
public class SSLGen {

	static final String ROOT_KEY_URL = "https://letsencrypt.org/certs/isrgrootx1.pem.txt";
	static final String INTERMEDIATE_KEY_URL = "https://letsencrypt.org/certs/lets-encrypt-x3-cross-signed.pem.txt";
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
		//if (1==1) return;
		new Thread(()-> {
			System.out.println("Updating Certificate Process");
			List<String> sites = getApplicableDomains();

			//We need to generate certs for this instance
			try {
				downloadBasicCerts();
				loadApplicationCertificate();
				connectToLetsEncrypt();
				getCertsForSites(sites);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}).start();

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
		FileOutputStream fos = new FileOutputStream(getCertPath()+file);
		fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
	}


	private void connectToLetsEncrypt() throws AcmeException {
		checkNotNull(applicationKeyPair, "We couldn't load an application key pair");
		session = new Session("acme://letsencrypt.org/staging", applicationKeyPair);
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
			FileWriter fw = new FileWriter(accountCertPath);
			KeyPairUtils.writeKeyPair(applicationKeyPair, fw);

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

	private void getCertsForSites(List<String> sites) throws Exception {
		checkNotNull(registration, "Must be registered to do this");
		for (String site : sites) {
			authorizeDomain(site);
			downloadCert(site);
		}


	}

	private void downloadCert(String site) throws Exception {
		checkNotNull(registration, "Need a registration to do this");
		KeyPair domainKeyPair = KeyPairUtils.createKeyPair(2048);
		FileWriter fw = new FileWriter(getCertPath()+site+"-priv.pem");
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
		X509Certificate[] chain = certificate.downloadChain();

		fw = new FileWriter(getCertPath()+site+"-combined.pem");
		CertificateUtils.writeX509CertificateChain(fw, cert, chain);


		fw = new FileWriter(getCertPath()+site+"-cert.pem");
		CertificateUtils.writeX509Certificate(cert, fw);

		fw = new FileWriter(getCertPath()+site+"-chain.pem");
		CertificateUtils.writeX509CertificateChain(fw, null, chain);



		KeyStore keyStore = KeyStore.getInstance("PKCS12");
		keyStore.load(null, null);//

		keyStore.setKeyEntry(site, domainKeyPair.getPrivate(),"password".toCharArray(), new java.security.cert.Certificate[]{cert});

		keyStore.store(new FileOutputStream(new File(getCertPath()+site+".p12")), "password".toCharArray());

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
		System.out.println("Waiting to authorize domain: "+domain+"/.well-known/acme-challenge/"+challenge.getToken());
		while (!currentlyValid && count < 100) {

			System.out.println(count+"/"+100);
			challenge.update();
			currentlyValid = challenge.getStatus() == Status.VALID;
			if (currentlyValid) break;
			Thread.sleep(timeout);
			count++;
			timeout *= 2;
			if (timeout > 10000) timeout = 10000;

		}

		if (currentlyValid) {
			System.out.println("Verified Domain: "+domain);
		} else {
			System.out.println("Could not verify domain: "+domain);
		}


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
