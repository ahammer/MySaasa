package com.mysaasa.core.marketing;

import com.mysaasa.Simple;
import com.mysaasa.SimpleImpl;
import com.mysaasa.core.hosting.service.HostingService;
import com.mysaasa.core.marketing.model.UserReferrals;
import com.mysaasa.core.organization.model.Organization;
import com.mysaasa.core.users.model.User;
import com.mysaasa.core.website.model.Domain;
import com.mysaasa.core.website.model.Website;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static com.mysaasa.MysaasaRequestMapperTest.TEST_DOMAIN;
import static org.junit.Assert.assertEquals;

public class MarketingServiceTest {
    private User userA;
    private User userB;
    //Rules of marketting
    //Users get X referrals
    //As a product owner, I should be able to generate referrals
    //As a product userA, I should be able to assign referrals


    @Before
    public void initialize() throws Exception {
        new SimpleImpl(true);

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
        organization.save();
        userA = new User();
        userA.setIdentifier("testUserA");
        userA.setOrganization(organization);

        userB = new User();
        userB.setIdentifier("testUserB");
        userB.setOrganization(organization);


    }


    @Test
    public void TestNewUserHasTwoReferrals() {
        MarketingService marketingService = Simple.getInstance().getInjector().getProvider(MarketingService.class).get();
        UserReferrals referrals = marketingService.findReferral(userA);
        assertEquals(referrals.getAvailableReferrals(),2);



    }

}