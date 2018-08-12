package com.mysaasa.core.marketing;

import com.mysaasa.core.marketing.model.UserReferrals;
import com.mysaasa.interfaces.annotations.SimpleService;
import net.bytebuddy.implementation.bytecode.Throw;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SimpleService
public class MarketingService {

	@Inject
	EntityManager em;

	public MarketingService() {
		super();
	}

	public UserReferrals findReferral(Long userId) {
		if (userId == null)
			return null;
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

	/**
	 * A Parent is making a referral to a Child
	 *
	 * @param parentId
	 * @param childId
	 */
	public void addReferral(final long parentId, final long childId) {
		final UserReferrals childRefferals = findReferral(childId);
		final UserReferrals parentReferrals = findReferral(parentId);


		if (childRefferals.getReferrals() != null
				|| childRefferals.getParentId() != null)  {
			throw new IllegalStateException(
					"User must be new to be added to referral system " +
					"(Already has referrals or a parent)");
		}


		//Set the childs Parent ID
		childRefferals.setParentId(parentId);

		//Add the Parent to the Childs ID list
		List<Long> parentReferralIdList = parentReferrals.getReferrals();
		if (parentReferralIdList == null)
			parentReferralIdList = new ArrayList<>();
		parentReferralIdList.add(childId);

		//Manage referal tree
		parentReferrals.setReferrals(parentReferralIdList);
		parentReferrals.decrementAvailableReferrals();

		int level = 0;
		UserReferrals currentReferrals = parentReferrals;
		while (currentReferrals != null) {

			currentReferrals.incrementLevel(level);

			save(currentReferrals);

			currentReferrals = findReferral(currentReferrals.getParentId());

			level++;
		}

		//Save if all else is OK
		save(childRefferals);
	}



}
