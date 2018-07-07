package com.mysaasa.api.responses;

import com.google.gson.annotations.Expose;

import java.util.Date;

public class SessionSummary {
    public static final SessionSummary NO_SESSION = new SessionSummary();

    @Expose
    final Date timestamp = new Date();
    int lengthSeconds = 0;

    @Expose
    LoginUserResponse.SecurityContext context;

    public SessionSummary() {
    }

    public int getLengthSeconds() {
        return lengthSeconds;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public LoginUserResponse.SecurityContext getContext() {
        return context;
    }

    public boolean isNullSession() {
        return this == NO_SESSION;
    }
}
