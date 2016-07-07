package com.mysaasa.api.responses;

import com.google.gson.annotations.Expose;

import java.util.Date;

/**
 * Created by Adam on 2/29/2016.
 */
public class LoginUserResponse extends SimpleResponse{

    private SessionSummary data;

    public SessionSummary getData() {
        return data;
    }

    /**
     * Set the current SessionSummary, sometimes we update this when we automatically
     * log in
     * @param data
     */
    public void setData(SessionSummary data) {
        this.data = data;
    }


    public static class SecurityContext {
        private com.mysaasa.api.model.User user;
        public SecurityContext(com.mysaasa.api.model.User user) {
            this.user = user;
        }

        public com.mysaasa.api.model.User getUser() {
            return user;
        }
    }

    public static class SessionSummary {
        @Expose
        final Date timestamp = new Date();
        int lengthSeconds;
        @Expose
        SecurityContext context;

        public SessionSummary() {
        }

        public int getLengthSeconds() {
            return lengthSeconds;
        }

        public Date getTimestamp() {
            return timestamp;
        }

        public SecurityContext getContext() {
            return context;
        }


    }

    @Override
    public String toString() {
        return "LoginUserResponse{" +
                "data=" + data +
                '}';
    }
}
