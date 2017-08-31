package com.mysaasa.api;

import com.mysaasa.Simple;
import com.mysaasa.SimpleImpl;
import com.mysaasa.api.model.ApiResult;
import com.mysaasa.api.model.ApiSuccess;
import com.mysaasa.interfaces.IApiService;
import com.mysaasa.interfaces.annotations.ApiCall;
import com.mysaasa.interfaces.annotations.SimpleService;
import org.apache.wicket.mock.MockWebRequest;
import org.apache.wicket.request.Url;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ApiHelperServiceTest {

    Simple simple;

    @Before
    public void setup() throws Exception {
        simple = new SimpleImpl();
    }

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

        MockWebRequest mockWebRequest = new MockWebRequest(Url.parse("http://test:8080/TestService/test"));
        ApiRequest apiRequest = service.getApiRequest(mockWebRequest);

        System.out.println(apiRequest);
        assertNotNull(apiRequest);
        ApiResult<?> result = apiRequest.invoke();
        assertEquals(result.toJson(), "{\"message\":\"ok\",\"success\":true,\"data\":\"test\"}");
    }

    @SimpleService
    public static class TestService implements IApiService {
        @ApiCall
        public ApiResult<String> test(){return new ApiSuccess<>("test");};
    }
}