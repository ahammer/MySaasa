package com.mysaasa.api;

import com.mysaasa.api.model.User;
import com.mysaasa.api.responses.CreateUserResponse;
import com.mysaasa.api.responses.LoginUserResponse;

import com.mysaasa.api.responses.LogoutResponse;
import com.mysaasa.api.responses.SessionSummary;
import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;

/**
 * This file handles authentication for the server.
 * You can login/create accounts and see if there is a authenticated user
 *
 * Created by Adam on 3/3/2016.
 */
public class AuthenticationManager {
    private final MySaasaClient mySaasa;

    public AuthenticationManager(MySaasaClient mySaasaClient) {
        this.mySaasa = mySaasaClient;
    }



    //We store the state in the behavior subjects, which we indirectly subscribe to
    BehaviorSubject<LoginUserResponse> loginResponseSubject = BehaviorSubject.create();
    BehaviorSubject<LogoutResponse> logoutResponseSubject = BehaviorSubject.create();
    BehaviorSubject<CreateUserResponse> createUserResponse = BehaviorSubject.create();

    /**
     * Log the lastAuthenticatedUser in. Will not execute if already in progress,
     *
     * @param username
     * @param password
     */
    public Observable<LoginUserResponse> login(final String username, final String password) {
        return mySaasa.retrofitGateway.loginUser(username, password);
    }


    public Observable<CreateUserResponse> createAccount(final String username, final String password) {
        mySaasa.retrofitGateway
                .createUser(username, password)
                .subscribe(createUserResponse::onNext);

        return createUserResponse;
    }

    public Observable<LogoutResponse> signOut() {
        mySaasa.retrofitGateway
                .logout()
                .subscribe(logoutResponseSubject::onNext);

        return logoutResponseSubject;
    }

    public SessionSummary getSessionSummary() {
        if (createUserResponse.hasValue()) {
            return createUserResponse.getValue().getData();
        } else if (loginResponseSubject.hasValue()) {
            return loginResponseSubject.getValue().getData();
        }

        return SessionSummary.NO_SESSION;
    }


    public User getAuthenticatedUser() {
        try {
            SessionSummary summary = getSessionSummary();
            LoginUserResponse.SecurityContext context = summary.getContext();
            return context.getUser();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return User.NULL_USER;
    }
}
