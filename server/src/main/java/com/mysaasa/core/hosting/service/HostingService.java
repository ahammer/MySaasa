package com.mysaasa.core.hosting.service;

import com.mysaasa.core.organization.model.Organization;
import com.mysaasa.core.website.model.ContentBinding;
import com.mysaasa.core.website.model.Domain;
import com.mysaasa.core.website.model.Website;
import com.mysaasa.core.website.services.WebsiteService;
import com.mysaasa.interfaces.annotations.SimpleService;
import com.mysaasa.Simple;
import org.apache.wicket.request.Url;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * This service manages website and hosting data.
 */
@SimpleService
public class HostingService {

	public static final String EDITOR_PREFIX = "edit_";

	public static HostingService get() {
		return Simple.get().getInjector().getProvider(HostingService.class).get();
	}

	public HostingService() {
		super();
	}

	private Map<String, Website> siteCache = new HashMap<String, Website>();

	/**
	 * Give a browser URL, find a website.
	 *
	 * There is a cache to speed this up.
	 *
	 * @param clientUrl
	 * @return
	 */
	public Website findWebsite(Url clientUrl) {
		checkNotNull(clientUrl);
		String host = clientUrl.getHost();
		if (HostingService.isSessionLinked(host)) {
			String real_domain = HostingService.RealDomain(host);
			String session_part = HostingService.Session(host);
			host = real_domain;
		}

		synchronized (siteCache) {
			Website w = siteCache.get(host);
			if (w != null)
				return w;
			EntityManager em = Simple.getEm();
			if (em == null)
				return null;
			Domain domain = HostingService.get().findDomain(host);
			final Query q = em.createQuery("SELECT W FROM Website W WHERE W.production=:domain OR W.staging=:domain OR :domainObj MEMBER OF W.domains")
					.setParameter("domain", host)
					.setParameter("domainObj", domain);
			final List<Website> websites = q.getResultList();
			if (websites.size() >= 1)
				w = websites.get(0);
			em.close();
			siteCache.put(host, w);
			return w;
		}
	}

	public static String Session(String clientUrl) {
		String without_prefix = clientUrl.substring(HostingService.EDITOR_PREFIX.length());
		if (Simple.isLocalDevMode()) {
			return "";
		}
		return without_prefix.substring(0, without_prefix.indexOf("_"));

	}

	public static String RealDomain(String clientUrl) {
		String without_prefix = clientUrl.substring(HostingService.EDITOR_PREFIX.length());
		if (Simple.isLocalDevMode()) {
			return without_prefix;
		} else {
			String session_part = without_prefix.substring(0, without_prefix.indexOf("_"));
			return without_prefix.substring(session_part.length() + 1);
		}
	}

	/**
	 * This will return all websites registered to the system
	 *
	 * @return
	 */
	public List<Website> getWebsites() {
		EntityManager em = Simple.getEm();
		List<Website> results = em.createQuery("SELECT W FROM Website W").getResultList();
		em.close();
		return results;
	}

	/**
	 * Delete a website,
	 * @param website
	 */
	public void deleteWebsite(Website website) {
		EntityManager entityManager = Simple.getEm();
		final Website trackedWebsite = entityManager.find(Website.class, website.getId());
		//Delete all blog posts

		entityManager.getTransaction().begin();
		for (ContentBinding b : WebsiteService.get().getBindings(trackedWebsite)) {
			WebsiteService.get().deleteContentBinding(b);
		}

		entityManager.remove(trackedWebsite);
		entityManager.flush();
		entityManager.getTransaction().commit();
		entityManager.close();
	}

	public Website saveWebsite(Website website) {
		checkNotNull(website);
		EntityManager em = Simple.getEm();
		em.getTransaction().begin();
		Website tracked = em.merge(website);
		em.flush();
		em.getTransaction().commit();
		em.close();
		return tracked;
	}

	public Domain saveDomain(Domain domain) {
		checkNotNull(domain);
		EntityManager em = Simple.getEm();
		em.getTransaction().begin();
		Domain tracked = em.merge(domain);
		em.flush();
		em.getTransaction().commit();
		em.close();
		return tracked;
	}

	/**
	 * Finds a website for a file
	 *
	 * To make sure it works cross platform, make sure to always use / instead of \ in
	 * the SIMPLE_CONFIG and any other path's you might define.]
	 *
	 * @param templateFile
	 * @return
	 */
	public Website findWebsite(File templateFile) {
		//Preconditions
		checkNotNull(templateFile);
		checkArgument(templateFile.exists(), "File Needs to exist: " + new Exception().getStackTrace()[0].getMethodName() + " " + templateFile.getAbsolutePath());

		//The base path to the websites folder
		//The absolute file path to the file you are checking
		String basePath = Simple.get().getConfigPath() + "websites/";
		String filePath = templateFile.getAbsolutePath();

		//Normalize the paths, helps with cross platform
		basePath = normalizePath(basePath);
		filePath = normalizePath(filePath);

		checkArgument(filePath.contains(basePath), "File needs to be in the base website folder: " + basePath);

		//www.test.com/path/to/file.html
		//www.test.com
		//Find website by http://www.website.com
		String relative_web_and_file = filePath.substring(basePath.length());
		int position_of_slash = relative_web_and_file.indexOf('/');
		if (position_of_slash == -1)
			return findWebsite(Url.parse("http://" + relative_web_and_file)); //No / Found, let's just assume it a website root path
		return findWebsite(Url.parse("http://" + relative_web_and_file.substring(0, position_of_slash)));
	}

	/**
	 * Helper to normalize paths in the filenames, since different OS's have different path structures
	 *
	 * @param substring
	 * @return
	 */
	public static String normalizePath(String substring) {
		return substring.replace('\\', '/');
	}

	public Website findWebsite(String domain) {
		EntityManager em = Simple.getEm();
		List<Website> results = em.createQuery("SELECT W FROM Website W WHERE LOWER(W.production)=:domain").setParameter("domain", domain.toLowerCase()).getResultList();
		em.close();
		if (results.size() != 1)
			return null;
		return results.get(0);
	}

	public List<Domain> createDomains(List<String> domains) {
		ArrayList<Domain> list = new ArrayList<>();
		for (String domain : domains) {
			Domain d = findDomain(domain);
			if (d != null) {
				list.add(d);
				continue;
			} else {
				d = saveDomain(new Domain(domain));
				list.add(d);
			}
		}
		return list;
	}

	public Domain findDomain(String domain) {
		EntityManager em = Simple.getEm();
		List<Domain> results = em.createQuery("SELECT D FROM Domain D WHERE D.domain=:domain").setParameter("domain", domain).getResultList();
		em.close();
		if (results.size() != 1)
			return null;
		return results.get(0);
	}

	private boolean domainExists(String domain) {
		return findDomain(domain) != null;
	}

	public List<Website> getWebsites(Organization organization) {
		EntityManager em = Simple.getEm();
		List<Website> results = em.createQuery("SELECT W FROM Website W WHERE W.organization=:organization").setParameter("organization", organization).getResultList();
		em.close();
		return results;
	}

	public static boolean isSessionLinked(String host) {
		return host.startsWith(HostingService.EDITOR_PREFIX);
	}
}
