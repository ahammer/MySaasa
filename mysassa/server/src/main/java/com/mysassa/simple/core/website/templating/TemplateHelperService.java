package com.mysassa.simple.core.website.templating;

import com.mysassa.simple.Simple;
import com.mysassa.simple.interfaces.ITemplateService;
import com.mysassa.simple.core.website.model.Website;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Registers the TemplateHelpers it finds
 *
 * Does a look up for TemplateEntryPoint, looks up TEMPLATE_SHORT_NAME on those classes
 *
 * They should also be ITemplateService, and should be registered with Dependency injection similar to to others
 *
 * This doesn't directly bind, it only looks up and registers the relevant interfaces, Templating usings DI to access
 * the implementation
 *
 * It also manages the RequestProperties thread local, which is where a TemplateEngines configuration data will live
 *
 */
public class TemplateHelperService {
	private static ThreadLocal<RequestProperties> mRequestProperties = new ThreadLocal();
	public static Map<String, Class> serviceMap = new ConcurrentHashMap();

	public static Map<String, Class> getServiceMap() {
		return serviceMap;
	}

	public static void bindTemplateService(ITemplateService service) {

		if (serviceMap.containsKey(service.getTemplateInterfaceName()))
			throw new IllegalArgumentException("Can not bind twice: " + service.getTemplateInterfaceName() + " " + service.getClass() + " " + serviceMap.get(service.getTemplateInterfaceName()).getClass());
		serviceMap.put(service.getTemplateInterfaceName(), service.getClass());
	}

	public static TemplateHelperService get() {
		return Simple.get().getInjector().getProvider(TemplateHelperService.class).get();
	}

	public static class RequestProperties {
		public final boolean debugMode;
		public final Website website;

		public RequestProperties(boolean debugMode, Website website) {
			this.debugMode = debugMode;
			this.website = website;
		}
	}

	public TemplateHelperService() {}

	/**
	 * Should clear this at the end of a request, as it should not be needed
	 *
	 */
	public void clearRequestProperties() {
		mRequestProperties.remove();
	}

	/**
	 * this is backed by a ThreadLocal, so that each request should have it's own data
	 * @param debugMode
	 * @param website
	 */
	public void setRequestProperties(boolean debugMode, Website website) {
		mRequestProperties.set(new RequestProperties(debugMode, website));
	}

	public RequestProperties getRequestProperties() {
		return mRequestProperties.get();
	}
}
