package com.mysaasa.api;

import com.mysaasa.api.messages.LoginStateChanged;
import com.mysaasa.api.model.User;
import com.mysaasa.api.observables.CreateAccountObservableBase;
import com.mysaasa.api.observables.LoginObservableBase;
import com.mysaasa.api.observables.PushIdGenerator;
import com.mysaasa.api.responses.LoginUserResponse;

import java.io.IOException;
import java.util.Date;

import retrofit2.Response;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * This file handles authentication for the server.
 * You can login/create accounts and see if there is a authenticated user
 *
 * Created by Adam on 3/3/2016.
 */
public class AuthenticationManager {
    public final MySaasaClient mySaasa;

    private LoginObservableBase loginObservableBase;
    private CreateAccountObservableBase createAccountObservableBase;
    public PushIdGenerator pushIdGenerator;

    public AuthenticationManager(MySaasaClient mySaasaClient) {
        this.mySaasa = mySaasaClient;
    }

    /**
     * Log the lastAuthenticatedUser in. Will not execute if already in progress,
     *
     * @param username
     * @param password
     */
    public Observable<LoginUserResponse> login(final String username, final String password) {
        return Observable.create(loginObservableBase = new LoginObservableBase(this, username, password)).subscribeOn(Schedulers.io());
    }

    public Observable<com.mysaasa.api.responses.CreateUserResponse> createAccount(final String username, final String password) {
        return Observable.create(createAccountObservableBase = new CreateAccountObservableBase(this, username, password)).subscribeOn(Schedulers.io());
    }

    public void signOut() {
        loginObservableBase = null;
        createAccountObservableBase = null;
        mySaasa.bus.post(new LoginStateChanged());
    }

    public LoginUserResponse.SessionSummary getSessionSummary() {
        try {
            if (createAccountObservableBase != null)
                return createAccountObservableBase.getResponse().getData();

            if (loginObservableBase != null)
                return loginObservableBase.getResponse().getData();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    private void setSessionSummary(LoginUserResponse.SessionSummary data) {
        if (createAccountObservableBase != null)
            createAccountObservableBase.getResponse().setData(data);

        if (loginObservableBase != null)
            loginObservableBase.getResponse().setData(data);
    }

    public User getAuthenticatedUser() {
        try {
            LoginUserResponse.SessionSummary summary = getSessionSummary();
            LoginUserResponse.SecurityContext context = summary.getContext();
            return context.getUser();
        } catch (Exception e) {
            e.printStackTrace();
            //Do Nothing
        }
        return null;
    }


    public void setPushIdGenerator(PushIdGenerator pushIdGenerator) {
        this.pushIdGenerator = pushIdGenerator;
    }

    //Blocking Call, call from another thread
    //This signs in you in again, if the session is expired.
    public void refreshIfNecessary() {
        LoginUserResponse.SessionSummary sessionSummary = getSessionSummary();
        if (sessionSummary != null) {
            int seconds = sessionSummary.getLengthSeconds();
            final Date expiry = new Date(sessionSummary.getTimestamp().getTime() + (seconds * 1000));
            if (expiry.before(new Date())) {
                String username = null;
                String password = null;
                if (createAccountObservableBase != null) {
                    username = createAccountObservableBase.getUsername();
                    password = createAccountObservableBase.getPassword();
                } else if (loginObservableBase != null) {
                    username = loginObservableBase.getUsername();
                    password = loginObservableBase.getPassword();
                }

                if (username != null && password != null) {
                    try {
                        Response<LoginUserResponse> response = mySaasa.gateway.loginUser(username, password).execute();
                        setSessionSummary(response.body().getData());
                    } catch (IOException e) {
                        throw new IllegalStateException("Could not log back in");
                    }
                }
            }
        }
    }



}
