package com.mysaasa.core.website.templating;

import com.mysaasa.MySaasa;
import com.mysaasa.core.website.model.Website;
import com.mysaasa.interfaces.ITemplateService;
import com.mysaasa.interfaces.annotations.SimpleService;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Registers the TemplateHelpers it finds
 *
 * Does a look up for TemplateEntryPoint, looks up TEMPLATE_SHORT_NAME on those classes
 *
 * They should also be ITemplateService, and should be registered with Dependency injection similar to to others
 *
 * This doesn't directly bind, it only looks up and registers the relevant interfaces, Templating usings DI to access the implementation
 *
 * It also manages the RequestProperties thread local, which is where a TemplateEngines configuration data will live
 *
 */
@SimpleService
public class TemplateHelperService {
	private ThreadLocal<RequestProperties> mRequestProperties = new ThreadLocal();
	public Map<String, Class> serviceMap = new ConcurrentHashMap();

	public Map<String, Class> getServiceMap() {
		return serviceMap;
	}

	public void bindTemplateService(ITemplateService service) {

		if (serviceMap.containsKey(service.getTemplateInterfaceName()))
			throw new IllegalArgumentException("Can not bind twice: " + service.getTemplateInterfaceName() + " " + service.getClass() + " " + serviceMap.get(service.getTemplateInterfaceName()).getClass());
		serviceMap.put(service.getTemplateInterfaceName(), service.getClass());
	}

	public static TemplateHelperService get() {
		return MySaasa.getInstance().getInjector().getProvider(TemplateHelperService.class).get();
	}

	public static class RequestProperties {
		public final boolean debugMode;
		public final Website website;

		public RequestProperties(boolean debugMode, Website website) {
			this.debugMode = debugMode;
			this.website = website;
		}
	}

	public TemplateHelperService() {
		System.out.println("Initializing Template Helper Service");
	}

	/**
	 * Should clear this at the end of a request, as it should not be needed
	 *
	 */
	public void clearRequestProperties() {
		mRequestProperties.remove();
	}

	/**
	 * this is backed by a ThreadLocal, so that each request should have it's own data
	 * 
	 * @param debugMode
	 *            dm
	 * @param website
	 *            website
	 */
	public void setRequestProperties(boolean debugMode, Website website) {
		mRequestProperties.set(new RequestProperties(debugMode, website));
	}

	public RequestProperties getRequestProperties() {
		return mRequestProperties.get();
	}
}
