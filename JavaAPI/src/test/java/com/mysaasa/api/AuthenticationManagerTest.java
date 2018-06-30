package com.mysaasa.api;

import com.mysaasa.MySaasaDaemon;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class AuthenticationManagerTest {

    @Before
    public void Setup() throws Exception {
        MySaasaDaemon.main(new String[]{});
    }

    @Test
    public void login() throws InterruptedException {
        Thread.sleep(1000);
    }

    @Test
    public void createAccount() {
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