package com.mysaasa.api.responses;

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
     * @param data  Information about the Session (start time, length, user)
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

    @Override
    public String toString() {
        return "LoginUserResponse{" +
                "data=" + data +
                '}';
    }
}
