package com.mysassa.simple.core.media.services;

import com.mysassa.simple.Simple;
import com.mysassa.simple.interfaces.annotations.SimpleService;
import com.mysassa.simple.core.media.model.Media;

import javax.persistence.EntityManager;
import java.util.List;

@SimpleService
public class MediaService {

	public MediaService() {
		super();
	}

	public static MediaService get() {
		return Simple.get().getInjector().getProvider(MediaService.class).get();
	}

	public List getMedia() {
		EntityManager em = Simple.getEm();
		List<Media> results = em.createQuery("SELECT M FROM Media M").getResultList();
		em.close();
		return results;
	}

	public Media saveMedia(Media m) {
		EntityManager em = Simple.getEm();
		em.getTransaction().begin();
		Media tracked = em.merge(m);
		em.flush();
		em.getTransaction().commit();
		em.close();
		return tracked;

	}

	/**
	 * @param uid
	 * @return Media that matches this UID
	 */
	public Media findByUid(String uid) {
		EntityManager em = Simple.getEm();
		List<Media> results = em.createQuery("SELECT M FROM Media M WHERE M.uid=:uid").setParameter("uid", uid).getResultList();
		em.close();
		if (results.size() == 0)
			return null; //TODO return a dummy media, 404 PNG or something like that
		return results.get(0);
	}

}
