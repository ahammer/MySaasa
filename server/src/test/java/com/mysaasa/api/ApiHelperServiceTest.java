package com.mysaasa.api;

import com.mysaasa.Simple;
import com.mysaasa.SimpleImpl;
import com.mysaasa.api.model.ApiError;
import com.mysaasa.api.model.ApiResult;
import com.mysaasa.test.mocks.TestService;
import org.apache.wicket.mock.MockWebRequest;
import org.apache.wicket.request.Url;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ApiHelperServiceTest {

    private Simple simple;
    private ApiHelperService service;

    @Before
    public void setup() throws Exception {
        simple = new SimpleImpl();
        service = new ApiHelperService();
        TestService mockApiService = new TestService();
        service.bindApiService(mockApiService);
        Map<String, ApiMapping> mapping = service.getPathMapping();
        assertEquals(mapping.size(), 5);
        assertEquals(mapping.get("TestService/test").getMethod(), mockApiService.getClass().getMethod("test"));
        assertTrue(service.isApiPathBound("TestService/test"));
        assertFalse(service.isApiPathBound("NotA/Service"));
    }

    @Test
    public void testSimpleFunction() throws Exception {
        MockWebRequest mockWebRequest = new MockWebRequest(Url.parse("http://test:8080/TestService/test"));
        ApiRequest apiRequest = service.getApiRequest(mockWebRequest);
        ApiResult<?> result = apiRequest.invoke();
        assertEquals(result.toJson(), "{\"message\":\"ok\",\"success\":true,\"data\":\"test\"}");
    }

    @Test
    public void testTwoArg() throws Exception {
        MockWebRequest mockWebRequest = new MockWebRequest(Url.parse("http://test:8080/TestService/addTwo"));
        mockWebRequest.getPostParameters().addParameterValue("a", "3");
        mockWebRequest.getPostParameters().addParameterValue("b", "5");
        ApiRequest apiRequest = service.getApiRequest(mockWebRequest);
        ApiResult<?> result = apiRequest.invoke();
        assertEquals(result.toJson(), "{\"message\":\"ok\",\"success\":true,\"data\":8}");
    }

    @Test
    public void testStringArg() throws Exception {
        MockWebRequest mockWebRequest = new MockWebRequest(Url.parse("http://test:8080/TestService/stringStub"));
        mockWebRequest.getPostParameters().addParameterValue("string", "TEST");
        ApiRequest apiRequest = service.getApiRequest(mockWebRequest);
        ApiResult<?> result = apiRequest.invoke();
        assertEquals(result.toJson(), "{\"message\":\"ok\",\"success\":true,\"data\":\"TEST\"}");
    }

    @Test
    public void testTwoArgsNoneProvided() throws Exception {
        MockWebRequest mockWebRequest = new MockWebRequest(Url.parse("http://test:8080/TestService/addTwo"));
        ApiRequest apiRequest = service.getApiRequest(mockWebRequest);
        ApiResult<?> result = apiRequest.invoke();
        assertFalse(result.isSuccess());
    }

    @Test
    public void testBadArgNames() throws Exception {
        MockWebRequest mockWebRequest = new MockWebRequest(Url.parse("http://test:8080/TestService/addTwo"));
        mockWebRequest.getPostParameters().addParameterValue("c", "3");
        mockWebRequest.getPostParameters().addParameterValue("d", "5");
        ApiRequest apiRequest = service.getApiRequest(mockWebRequest);
        ApiResult<?> result = apiRequest.invoke();
        assertFalse(result.isSuccess());
    }


    @Test
    public void testBadUrl() throws Exception {
        MockWebRequest mockWebRequest = new MockWebRequest(Url.parse("http://test:8080/Junk/Junk"));
        ApiRequest apiRequest = service.getApiRequest(mockWebRequest);
        ApiResult<?> result = apiRequest.invoke();
        assertFalse(result.isSuccess());
    }

    @Test
    public void testIncorrectArgCount() throws Exception {
        MockWebRequest mockWebRequest = new MockWebRequest(Url.parse("http://test:8080/TestService/addTwo"));
        mockWebRequest.getPostParameters().addParameterValue("a", "3");
        mockWebRequest.getPostParameters().addParameterValue("b", "5");
        mockWebRequest.getPostParameters().addParameterValue("c", "5");
        ApiRequest apiRequest = service.getApiRequest(mockWebRequest);
        ApiResult<?> result = apiRequest.invoke();
        assertFalse(result.isSuccess());
    }

    @Test
    public void testIncorrectArgCount2() throws Exception {
        MockWebRequest mockWebRequest = new MockWebRequest(Url.parse("http://test:8080/TestService/test"));
        mockWebRequest.getPostParameters().addParameterValue("a", "3");
        mockWebRequest.getPostParameters().addParameterValue("b", "5");
        mockWebRequest.getPostParameters().addParameterValue("c", "5");
        ApiRequest apiRequest = service.getApiRequest(mockWebRequest);
        ApiResult<?> result = apiRequest.invoke();
        assertFalse(result.isSuccess());
    }

    @Test
    public void testFailingMethod() throws Exception {
        MockWebRequest mockWebRequest = new MockWebRequest(Url.parse("http://test:8080/TestService/throwsUp"));
        ApiRequest request = service.getApiRequest(mockWebRequest);
        ApiResult<?> result = request.invoke();
        assertTrue(result instanceof ApiError);
    }


    @Test
    public void testFailingMethod2() throws Exception {
        MockWebRequest mockWebRequest = new MockWebRequest(Url.parse("http://test:8080/TestService/throwsUpWithNullMessage"));
        ApiRequest request = service.getApiRequest(mockWebRequest);
        ApiResult<?> result = request.invoke();
        assertTrue(result instanceof ApiError);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBadMethodRegistration() throws Exception {
        service.registerMethod(TestService.class.getMethod("noAnnotation"));
    }

    @Test
    public void testGetPaths() {
        String[] paths = service.getPaths();
        assertNotNull(paths);
        assertTrue(paths.length>2);
    }

    @Test
    public void testInjection() {
        assertNotNull(ApiHelperService.get());
    }

    @Test
    public void testGetMapping() {
        assertNotNull(service.getMapping("TestService/throwsUp"));
    }

}