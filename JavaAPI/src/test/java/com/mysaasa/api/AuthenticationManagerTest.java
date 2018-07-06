package com.mysaasa.api;

import com.mysaasa.MySaasaDaemon;
import com.mysaasa.api.responses.CreateUserResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class AuthenticationManagerTest {

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
    public void login() throws InterruptedException {
        Thread.sleep(5000);
    }

    @Test
    public void createAccount() {
        CreateUserResponse response = client.getAuthenticationManager()
                .createAccount("test2", "testpassword")
                .blockingFirst();

        assertNotNull(response);
        assertTrue(response.getMessage(), response.isSuccess());
    }

    @Test
    public void signOut() {
    }

    @Test
    public void getSessionSummary() {
    }

    @Test
    public void getAuthenticatedUser() {
    }
}