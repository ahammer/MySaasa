package com.mysaasa.api;

import com.mysaasa.MySaasaDaemon;
import com.mysaasa.api.model.User;
import com.mysaasa.api.model.UserReferralData;
import com.mysaasa.api.responses.*;
import org.junit.*;

import static org.junit.Assert.*;

/**
 * These tests spawn up a server with in-memory/blank DB for the suite
 * You can then use the client against to verify on the "localhost" domain
 */
public class MySaasaClientTests {

    private static MySaasaClient client;

    @BeforeClass
    public static void Setup() throws Exception {
        MySaasaDaemon.main(new String[]{"localmode"});
        client = new MySaasaClient("localhost", 80, "http");
    }


    @Test
    public void createAccountThenSignoutThenLoginTest() throws InterruptedException {
        //Check session is blank
        SessionSummary summary = client.observeSessionSummary().blockingFirst();
        assertEquals(summary, SessionSummary.NO_SESSION);

        client.createUser("test1", "testpassword").blockingFirst();
        summary = client.observeSessionSummary().blockingFirst();
        assertNotEquals(summary, SessionSummary.NO_SESSION);

        client.logout().blockingFirst();
        summary = client.observeSessionSummary().blockingFirst();
        assertEquals(summary, SessionSummary.NO_SESSION);

        client.loginUser("test1", "testpassword").blockingFirst();

        summary = client.observeSessionSummary().blockingFirst();
        assertNotEquals(summary, SessionSummary.NO_SESSION);
        assertEquals(summary.getContext().getUser().identifier, "test1");
    }


    @Test
    public void testReferralSystem() {
        SessionSummary userASummary = client.createUser("ReferralUserA", "testpassword").blockingFirst().getData();
        client.logout().blockingFirst();
        SessionSummary userBSummary = client.createUser("ReferralUserB", "testpassword").blockingFirst().getData();


        User userA = userASummary.getContext().getUser();
        User userB = userBSummary.getContext().getUser();

        AddReferralResponse addReferralResponse = client
                .addReferral(userA.id, userB.id)
                .blockingFirst();

        assertTrue(addReferralResponse.isSuccess());


        //Reverse should fail
        addReferralResponse = client
                .addReferral(userA.id, userB.id)
                .blockingFirst();
        assertFalse(addReferralResponse.isSuccess());


        //Dup should fail
        addReferralResponse = client
                .addReferral(userB.id, userA.id)
                .blockingFirst();
        assertFalse(addReferralResponse.isSuccess());

        UserReferralDataResponse response = client.getUserReferralData().blockingFirst();
        assertTrue(response.isSuccess());

        client.logout().blockingFirst();
        userASummary = client.loginUser("ReferralUserA", "testpassword").blockingFirst().getData();
        response = client.getUserReferralData().blockingFirst();
        assertTrue(response.isSuccess());


    }



}