package com.mysaasa.core.media.services;

;
import com.mysaasa.MySaasa;
import com.mysaasa.core.media.model.Media;
import com.mysaasa.interfaces.annotations.SimpleService;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.List;

@SimpleService
public class MediaService {

	@Inject
	EntityManager em;
	public MediaService() {
		super();
	}

	public static MediaService get() {
		return MySaasa.getInstance().getInjector().getProvider(MediaService.class).get();
	}

	public List getMedia() {
		List<Media> results = em.createQuery("SELECT M FROM Media M").getResultList();
		em.close();
		return results;
	}

	public Media saveMedia(Media m) {
		em.getTransaction().begin();
		Media tracked = em.merge(m);
		em.flush();
		em.getTransaction().commit();
		em.close();
		return tracked;

	}

	/**
	 * @param uid uid
	 * @return Media that matches this UID
	 */
	public Media findByUid(String uid) {
		List<Media> results = em.createQuery("SELECT M FROM Media M WHERE M.uid=:uid").setParameter("uid", uid).getResultList();
		em.close();
		if (results.size() == 0)
			return null; //TODO return a dummy media, 404 PNG or something like that
		return results.get(0);
	}

}
