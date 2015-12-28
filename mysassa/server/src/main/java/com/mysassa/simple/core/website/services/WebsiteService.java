package com.mysassa.simple.core.website.services;

import com.mysassa.simple.Simple;
import com.mysassa.simple.interfaces.annotations.SimpleService;
import com.mysassa.simple.core.website.model.Website;
import com.mysassa.simple.core.website.model.Content;
import com.mysassa.simple.core.website.model.ContentBinding;
import org.apache.commons.io.FileUtils;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.io.File;
import java.io.IOException;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The WebsiteService provides
 * Created by Adam on 2/26/14.
 */
@SimpleService
public class WebsiteService {

	public void saveContentBinding(ContentBinding contentBinding) {
		//("Saving a content binding");
		EntityManager em = Simple.getEm();
		try {
			em.getTransaction().begin();
			ContentBinding tracked = em.merge(contentBinding);
			em.persist(tracked);
			em.getTransaction().commit();
		} catch (Exception e) {
			e.printStackTrace();
			em.getTransaction().rollback();
		} finally {
			em.close();
		}

	}

	public ContentBinding findBinding(String name, Website w, String defaultText) {
		EntityManager em = Simple.getEm();
		Query q = em.createQuery("SELECT B FROM ContentBinding B WHERE B.website=:website AND B.name=:name");
		q.setParameter("name", name);
		q.setParameter("website", w);

		ContentBinding result = null;
		List<ContentBinding> bindingList = q.getResultList();
		if (bindingList.size() == 0) {
			ContentBinding b = new ContentBinding();
			b.setWebsite(w);
			b.setName(name);
			b.setContent(new Content());
			b.getContent().setBody(defaultText);
			em.close();
			return b;

		}
		try {
			result = (ContentBinding) q.getResultList().get(0);
		} catch (Exception e) {
			e.printStackTrace();
			em.close();
			throw e;
		}
		result.getContent();
		em.close();
		return result; //To change body of created methods use File | Settings | File Templates.
	}

	public static WebsiteService get() {
		return Simple.get().getInjector().getProvider(WebsiteService.class).get();
	}

	public void installTemplateIntoWebsiteStaging(Website theme, Website website) {
		File website_staging = website.calculateStagingRoot();
		File theme_root = theme.calculateProductionRoot();
		try {
			FileUtils.deleteDirectory(website_staging);
			website_staging.mkdirs();
			FileUtils.copyDirectory(theme_root, website_staging);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void deployStaging(Website website) {
		checkNotNull(website);
		File website_staging = website.calculateStagingRoot();
		File website_root = website.calculateProductionRoot();
		try {
			FileUtils.deleteDirectory(website_root);
			website_root.mkdirs();
			FileUtils.copyDirectory(website_staging, website_root);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void copyWebsite(Website source, Website dest) {
		checkNotNull(source);
		checkNotNull(dest);
		File sourceFile = source.calculateProductionRoot();
		try {
			FileUtils.deleteDirectory(dest.calculateProductionRoot());
			FileUtils.deleteDirectory(dest.calculateStagingRoot());
			sourceFile.mkdirs();
			FileUtils.copyDirectory(sourceFile, dest.calculateProductionRoot());
			FileUtils.copyDirectory(sourceFile, dest.calculateStagingRoot());
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/** Installs the getting started stuff */
	public void installGettingStarted(Website dest) {
		checkNotNull(dest);
		File sourceFile = new File(Simple.getConfigPath() + "websites/gettingstarted." + Simple.getBaseDomain());
		try {
			FileUtils.deleteDirectory(dest.calculateProductionRoot());
			FileUtils.deleteDirectory(dest.calculateStagingRoot());
			sourceFile.mkdirs();
			FileUtils.copyDirectory(sourceFile, dest.calculateProductionRoot());
			FileUtils.copyDirectory(sourceFile, dest.calculateStagingRoot());
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void deleteContentBinding(ContentBinding b) {
		EntityManager em = Simple.getEm();
		try {
			em.getTransaction().begin();
			ContentBinding tracked = em.merge(b);
			em.remove(tracked);
			//em.persist(tracked);
			em.getTransaction().commit();
		} catch (Exception e) {
			e.printStackTrace();
			em.getTransaction().rollback();
		} finally {
			em.close();
		}

	}

	public List<ContentBinding> getBindings(Website w) {

		EntityManager em = Simple.getEm();
		Query q = em.createQuery("SELECT B FROM ContentBinding B WHERE B.website=:website");
		q.setParameter("website", w);
		List<ContentBinding> bindingList = q.getResultList();
		em.close();
		return bindingList;

	}
}
