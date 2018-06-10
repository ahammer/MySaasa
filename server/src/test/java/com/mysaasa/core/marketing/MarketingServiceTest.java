package com.mysaasa.core.marketing;

import com.mysaasa.Simple;
import com.mysaasa.SimpleImpl;
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

import static com.mysaasa.MysaasaRequestMapperTest.TEST_DOMAIN;
import static org.junit.Assert.assertEquals;

public class MarketingServiceTest {
    private User userA;
    private User userB;
    private User userC;
    //Rules of marketting
    //Users get X referrals
    //As a product owner, I should be able to generate referrals
    //As a product userA, I should be able to assign referrals


    @Before
    public void initialize() throws Exception {
        new WicketTester(new SimpleImpl(true));

        HostingService service = HostingService.get();
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
        userC.setIdentifier("testUserB");
        userC.setOrganization(organization);


        userA = UserService.get().saveUser(userA);
        userB = UserService.get().saveUser(userB);
        userC = UserService.get().saveUser(userC);

    }


    @Test
    public void TestNewUserHasTwoReferrals() {
        MarketingService marketingService = Simple.getInstance().getInjector().getProvider(MarketingService.class).get();
        UserReferrals referrals = marketingService.findReferral(userA.id);
        assertEquals(referrals.getAvailableReferrals(),2);
    }

    @Test
    public void TestReferralProcess() {
        MarketingService marketingService = Simple.getInstance().getInjector().getProvider(MarketingService.class).get();
        marketingService.addReferral(userA.id, userB.id);
        UserReferrals referrals = marketingService.findReferral(userA.id);
        assertEquals(referrals.getAvailableReferrals(),1);

        //Add same one twice
        marketingService.addReferral(userA.id, userB.id);
        referrals = marketingService.findReferral(userA.id);
        assertEquals(referrals.getAvailableReferrals(),1);

        //Add a second one
        marketingService.addReferral(userA.id, userC.id);
        referrals = marketingService.findReferral(userA.id);
        assertEquals(referrals.getAvailableReferrals(),0);
    }

}