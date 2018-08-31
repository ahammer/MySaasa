package com.mysaasa.api.responses;

import com.mysaasa.server.ErrorCode;

/**
 * Created by Adam on 2/29/2016.
 */
public class SimpleResponse {
    protected boolean success;
    protected String message = "";
    protected ErrorCode errorcode = ErrorCode.UNKNOWN_ERROR;
    protected String stacktrace = "";

    public SimpleResponse(){}
    public boolean isSuccess() {
        return this.success;
    }
    public String getMessage() { return message; }

    public ErrorCode getErrorCode() {
        return errorcode;
    }

    public String getStacktrace() {
        return stacktrace;
    }
}
