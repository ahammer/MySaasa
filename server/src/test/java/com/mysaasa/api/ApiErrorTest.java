package com.mysaasa.api;

import org.junit.Test;

import static org.junit.Assert.*;

public class ApiErrorTest {

    @Test
    public void testApiError() throws Exception {
        Exception e = new RuntimeException("Exception");
        ApiError<Exception> error = new ApiError(e);

        //The Message is the Exception toString()
        assertEquals(error.message, e.toString());

        //The Data is null
        assertEquals(error.getData(), null);
    }

    @Test
    public void testApiErrorNPE() throws Exception {
        Exception e = new NullPointerException();
        ApiError<Exception> error = new ApiError(e);

        //The Message is the Exception toString()
        assertEquals(error.message, ApiError.NPE_ERROR_MESSAGE);

        //The Data is null
        assertEquals(error.getData(), null);

    }
}