package com.mysaasa.api.model;

import org.junit.Test;

import static org.junit.Assert.*;

public class ApiErrorTest {

    @Test
    public void testApiError() throws Exception {
        Exception e = new RuntimeException("Exception");
        ApiError<Exception> error = new ApiError(e);
        assertEquals(error.message, e.toString());
        assertEquals(error.getData(), null);
        assertFalse(error.isSuccess());
    }

    @Test
    public void testApiErrorNPE() throws Exception {
        Exception e = new NullPointerException();
        ApiError<Exception> error = new ApiError(e);
        assertEquals(error.message, ApiError.NPE_ERROR_MESSAGE);
        assertEquals(error.getData(), null);
        assertFalse(error.isSuccess());
    }
}