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

    @Test
    public void testToJson() throws Exception {
        ApiSuccess apiSuccess = new ApiSuccess<>("Hello");
        String expectedJson = "{\"message\":\"ok\",\"success\":true,\"data\":\"Hello\"}";
        assertEquals(apiSuccess.toJson(), expectedJson);
        assertEquals(apiSuccess.toString(), expectedJson);
    }
}