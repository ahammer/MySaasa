package com.mysaasa.api.messages;

/**
 * Created by Adam on 1/20/2015.
 */
public class ErrorMessage {

    public final Exception e;

    public ErrorMessage(Exception e) {
        this.e = e;
    }
}
