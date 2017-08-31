package com.mysaasa.api;

import com.mysaasa.interfaces.IApiService;
import com.mysaasa.interfaces.annotations.ApiCall;
import org.junit.Test;

import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ApiHelperServiceTest {
    @Test
    public void apiHelperServiceTest() throws Exception {
        ApiHelperService service = new ApiHelperService();
        TestService mockApiService = new TestService();
        service.bindApiService(mockApiService);
        Map<String, ApiMapping> mapping = service.getPathMapping();
        assertEquals(mapping.size(), 1);
        assertEquals(mapping.get("TestService/test").getMethod(), mockApiService.getClass().getMethod("test"));
        assertTrue(service.isApiPathBound("TestService/test"));
        assertFalse(service.isApiPathBound("NotA/Service"));



    }

    class TestService implements IApiService {
        @ApiCall
        public void test(){};
    }
}