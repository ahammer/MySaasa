package com.mysaasa;

import com.mysaasa.api.ApiHelperService;
import com.mysaasa.api.ApiRequestHandler;
import com.mysaasa.core.hosting.service.HostingService;
import com.mysaasa.core.website.model.Domain;
import com.mysaasa.core.website.model.Website;
import com.mysaasa.test.mocks.TestService;
import org.apache.wicket.mock.MockWebRequest;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.cycle.RequestCycle;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by Adam on 9/2/2017.
 */
public class MysaasaRequestMapperTest {
	public static final String TEST_DOMAIN = "www.test.com";
	MysaasaRequestMapper mapper = new MysaasaRequestMapper();

	@Before
	public void initialize() throws Exception {
		new SimpleImpl(true);
		TestService mockApiService = new TestService();
		ApiHelperService.get().bindApiService(mockApiService);

		HostingService service = HostingService.get();
		Website testWebsite = new Website();
		testWebsite.setProduction("www.test.com");
		testWebsite.setStaging("www.staging.com");
		List<String> urls = Arrays.asList(TEST_DOMAIN);
		List<Domain> domains = service.createDomains(urls);
		testWebsite.setDomains(domains);
		service.saveWebsite(testWebsite);
	}

	@Test
	public void testCompatibleWebsite() throws Exception {
		Url parse = Url.parse("http://" + TEST_DOMAIN);
		MockWebRequest webRequest = new MockWebRequest(parse);
		int compatibility = mapper.getCompatibilityScore(webRequest);
		assertEquals(compatibility, MysaasaRequestMapper.MATCHING_SCORE);
	}

	@Test
	public void testIncompatibleWebsite() throws Exception {
		Url parse = Url.parse("http://bad" + TEST_DOMAIN);
		MockWebRequest webRequest = new MockWebRequest(parse);
		int compatibility = mapper.getCompatibilityScore(webRequest);
		assertEquals(compatibility, MysaasaRequestMapper.NO_MATCH);
	}

	@Test
	public void testApiResolution() throws Exception {
		Url parse = Url.parse("http://" + TEST_DOMAIN + "/TestService/test");
		MockWebRequest webRequest = new MockWebRequest(parse);
		int compatibility = mapper.getCompatibilityScore(webRequest);
		assertEquals(compatibility, MysaasaRequestMapper.MATCHING_SCORE);
		IRequestHandler result = mapper.mapRequest(webRequest);
		assertNotNull(result);
		assertEquals(result.getClass(), ApiRequestHandler.class);
		ApiRequestHandler handler = (ApiRequestHandler) result;
		assertEquals(handler.getResponseJson(), "{\"message\":\"ok\",\"success\":true,\"data\":\"test\"}");
	}

	@Test
	public void testJunk() throws Exception {
		Url parse = Url.parse("http://" + TEST_DOMAIN + "/Not/Real");
		MockWebRequest webRequest = new MockWebRequest(parse);
		int compatibility = mapper.getCompatibilityScore(webRequest);
		assertEquals(compatibility, MysaasaRequestMapper.MATCHING_SCORE);
		IRequestHandler result = mapper.mapRequest(webRequest);
		assertNull(result);
	}

	@Test
	public void testMapRequest() throws Exception {

	}
}
