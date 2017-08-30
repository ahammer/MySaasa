package com.mysaasa.api.model;

import org.junit.Test;

import static org.junit.Assert.*;

public class ApiSuccessTest {
    @Test
    public void testApiSuccess() {
        ApiSuccess<String> apiSuccess = new ApiSuccess("Hello");
        assertTrue(apiSuccess.isSuccess());
        assertEquals(apiSuccess.message, "ok");
        assertEquals(apiSuccess.getData(), "Hello");
    }

}