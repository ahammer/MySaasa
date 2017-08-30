package com.mysaasa.api.model;

import org.junit.Test;

import static org.junit.Assert.*;

public class ApiResultTest {
    @Test
    public void testApiResult() throws Exception {
        ApiResult<String> result = new ApiResult<String>("hello"){};
        assertEquals("hello", result.getData());
        assertEquals("ok", result.message);
    }

    @Test
    public void testCorrectType() throws Exception {
        assertTrue(new ApiResult<Integer>(10){}.getData() instanceof Integer);
        assertTrue(new ApiResult<String>("Hello"){}.getData() instanceof String);
    }
}