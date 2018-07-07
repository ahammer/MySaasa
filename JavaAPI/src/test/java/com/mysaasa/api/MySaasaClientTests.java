package com.mysaasa.api;

import com.mysaasa.MySaasaDaemon;
import com.mysaasa.api.model.User;
import com.mysaasa.api.responses.AddReferralResponse;
import com.mysaasa.api.responses.CreateUserResponse;
import com.mysaasa.api.responses.LoginUserResponse;
import com.mysaasa.api.responses.SessionSummary;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * These tests spawn up a server with in-memory/blank DB for each test
 * You can then use the client against to verify on the "localhost" domain
 */
public class MySaasaClientTests {

    private MySaasaClient client;

    @Before
    public void Setup() throws Exception {
        MySaasaDaemon.main(new String[]{"localmode"});
        client = new MySaasaClient("localhost", 8080, "http");
    }

    @After
    public void TearDown() throws Exception {
        MySaasaDaemon.stopNow();
    }

    @Test
    public void createAccountThenSignoutThenLoginTest() throws InterruptedException {
        //Check session is blank
        SessionSummary summary = client.observeSessionSummary().blockingFirst();
        assertEquals(summary, SessionSummary.NO_SESSION);

        client.createUser("test1", "testpassword");
        summary = client.observeSessionSummary().blockingFirst();
        assertNotEquals(summary, SessionSummary.NO_SESSION);

        client.logout();
        summary = client.observeSessionSummary().blockingFirst();
        assertEquals(summary, SessionSummary.NO_SESSION);

        client.loginUser("test1", "testpassword");

        summary = client.observeSessionSummary().blockingFirst();
        assertNotEquals(summary, SessionSummary.NO_SESSION);
        assertEquals(summary.getContext().getUser().identifier, "test1");
    }


    @Test
    public void testReferralSystem() {
        client.createUser("userA", "testpassword");
        SessionSummary userASummary = client.observeSessionSummary().blockingFirst();
        client.logout();

        client.createUser("userB", "testpassword");
        SessionSummary userBSummary = client.observeSessionSummary().blockingFirst();
        client.logout();

        User userA = userASummary.getContext().getUser();
        User userB = userBSummary.getContext().getUser();

        AddReferralResponse addReferralResponse = client
                .addReferral(userA.id, userB.id)
                .blockingFirst();

        assertTrue(addReferralResponse.isSuccess());


        //Duplicate should fail
        addReferralResponse = client
                .addReferral(userA.id, userB.id)
                .blockingFirst();

        assertFalse(addReferralResponse.isSuccess());



    }



}