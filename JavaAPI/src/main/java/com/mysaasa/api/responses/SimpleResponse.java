package com.mysaasa.api.responses;

/**
 * Created by Adam on 2/29/2016.
 */
public class SimpleResponse {
    protected boolean success;
    protected String message = "";
    public SimpleResponse(){}
    public boolean isSuccess() {
        return this.success;
    }
    public String getMessage() { return message; }
}
