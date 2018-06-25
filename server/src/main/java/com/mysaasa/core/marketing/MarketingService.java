package com.mysaasa.core.marketing;

import com.mysaasa.core.hosting.service.BaseInjectedService;
import com.mysaasa.core.marketing.model.UserReferrals;
import com.mysaasa.interfaces.annotations.SimpleService;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

@SimpleService
public class MarketingService extends BaseInjectedService {

	@Inject
	EntityManager em;

	public MarketingService() {
		super();
	}

	public UserReferrals findReferral(long userId) {
		final Query q = em.createQuery("SELECT U FROM UserReferrals U WHERE U.userId=:userId")
				.setParameter("userId", userId);
		final List<UserReferrals> referralsList = q.getResultList();
		if (referralsList.size() == 0) {

			UserReferrals referrals = new UserReferrals(userId);
			save(referrals);
			return referrals;
		}

		return referralsList.get(0);
	}

	private void save(UserReferrals referrals) {
		em.getTransaction().begin();
		em.persist(referrals);
		em.getTransaction().commit();
	}

	public void addReferral(long parentId, long childId) {

		UserReferrals userReferrals = findReferral(parentId);
		List<Long> idList = userReferrals.getReferrals();
		if (idList == null)
			idList = new ArrayList<>();
		if (idList.contains(childId))
			return;
		idList.add(childId);
		userReferrals.setReferrals(idList);
		userReferrals.decrementAvailableReferrals();
		save(userReferrals);

		UserReferrals childReferral = findReferral(childId);
		childReferral.setParentId(parentId);
		save(childReferral);

	}
}
