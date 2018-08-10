package com.mysaasa.core.marketing;

import com.mysaasa.MySaasa;
import com.mysaasa.core.hosting.service.HostingService;
import com.mysaasa.core.marketing.model.UserReferrals;
import com.mysaasa.core.organization.model.Organization;
import com.mysaasa.core.users.model.User;
import com.mysaasa.core.users.service.UserService;
import com.mysaasa.core.website.model.Domain;
import com.mysaasa.core.website.model.Website;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static com.mysaasa.MySaasa.getService;
import static com.mysaasa.MysaasaRequestMapperTest.TEST_DOMAIN;
import static org.junit.Assert.assertEquals;

public class MarketingServiceTest {
	private User userA;
	private User userB;
	private User userC;
	private User userD;

	//Rules of marketting
	//Users get X referrals
	//As a product owner, I should be able to generate referrals
	//As a product userA, I should be able to assign referrals

	@Before
	public void initialize() throws Exception {
		MySaasa.IN_MEMORY_DATABASE = true;
		MySaasa simple;
		new WicketTester(simple = new MySaasa());
//		ThreadContext.setApplication(simple);

		HostingService service = getService(HostingService.class);
		Website testWebsite = new Website();
		testWebsite.setProduction("www.test.com");
		testWebsite.setStaging("www.staging.com");
		List<String> urls = Arrays.asList(TEST_DOMAIN);
		List<Domain> domains = service.createDomains(urls);
		testWebsite.setDomains(domains);
		service.saveWebsite(testWebsite);

		Organization organization = new Organization();
		organization.setName("testOrg");
		organization = organization.save();
		userA = new User();
		userA.setIdentifier("testUserA");
		userA.setOrganization(organization);

		userB = new User();
		userB.setIdentifier("testUserB");
		userB.setOrganization(organization);

		userC = new User();
		userC.setIdentifier("testUserC");
		userC.setOrganization(organization);

		userD = new User();
		userD.setIdentifier("testUserD");
		userD.setOrganization(organization);

		userA = UserService.get().saveUser(userA);
		userB = UserService.get().saveUser(userB);
		userC = UserService.get().saveUser(userC);
		userD = UserService.get().saveUser(userD);

	}

	@Test
	public void TestNewUserHasTwoReferrals() {
		MarketingService marketingService = MySaasa.getInstance().getInjector().getProvider(MarketingService.class).get();
		UserReferrals referrals = marketingService.findReferral(userA.id);
		assertEquals(referrals.getAvailableReferrals(), 2);
	}

	@Test
	public void TestReferralProcess() {
		MarketingService marketingService = MySaasa.getInstance().getInjector().getProvider(MarketingService.class).get();
		marketingService.addReferral(userA.id, userB.id);
		marketingService.addReferral(userA.id, userC.id);

		UserReferrals referralsB = marketingService.findReferral(userB.id);
		assertEquals(referralsB.getParentId(), Long.valueOf(userA.id));
	}

	@Test(expected = IllegalStateException.class)
	public void testNoDupReferrals() {
		MarketingService marketingService = MySaasa.getInstance().getInjector().getProvider(MarketingService.class).get();
		marketingService.addReferral(userA.id, userB.id);
		marketingService.addReferral(userA.id, userC.id);
		marketingService.addReferral(userB.id, userC.id);

		UserReferrals referralsB = marketingService.findReferral(userB.id);
		assertEquals(referralsB.getParentId(), Long.valueOf(userA.id));
	}

	@Test
	public void testReferralPyramid() {
		MarketingService marketingService = MySaasa.getInstance().getInjector().getProvider(MarketingService.class).get();
		marketingService.addReferral(userA.id, userB.id);
		marketingService.addReferral(userA.id, userC.id);
		marketingService.addReferral(userB.id, userD.id);

		UserReferrals referralsA = marketingService.findReferral(userA.id);
		List<Integer> pyramid = referralsA.getPyramid();
		assertEquals(pyramid.get(0), (Integer) 2);
		assertEquals(pyramid.get(1), (Integer) 1);
	}
}
