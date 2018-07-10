package com.mysaasa.core;

import java.util.*;

import com.google.inject.Binding;
import com.mysaasa.api.ApiHelperService;
import com.mysaasa.core.website.templating.TemplateHelperService;
import com.mysaasa.interfaces.IClassPanelAdapter;
import com.mysaasa.interfaces.ITemplateService;
import com.mysaasa.Simple;

import com.mysaasa.interfaces.IApiService;
import org.reflections.Reflections;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Bootstraps the module loading, we do a lot here in a little code
 *
 * Basically, we look for SimpleService annotated classes, we bindTemplateService them to the system
 *
 * If they are a ApiService, we bind them with the ApiHelperService, which will make them available
 * to API urls, visible in /ApiGuide
 *
 * For templating, if it extends TemplateService and has a TEMPLATE_SIMPLE_NAME static string,
 * it will be bound to the Template engine and available at runtime.
 *
 * For templating, an additional annotation is required on the Interface that is to be exposed . See @TemplateEntryPoint
 *
 *
 * TO bindTemplateService new things, simple drop a ISimpleService, ITemplateService or IApiService into the module and let it go
 *
 * You might want to bindTemplateService DI hooks in the AbstractSimpleGuiceModule if you are going to use injection to access
 *
 * Templating uses injection to getInstance at these classes, so TemplateServices need injection hooks.
 */

public class ModuleManager {

	static ModuleManager INSTANCE;

	public static ModuleManager get() {
		if (INSTANCE == null) {
			INSTANCE = new ModuleManager();
		}

		return INSTANCE;
	}

	private final ArrayList<AbstractModule> modules = new ArrayList<AbstractModule>();
	private final HashMap<String, Object> services = new HashMap();
	private volatile boolean initialized = false;
	private final HashMap<Class, IClassPanelAdapter> classPanelAdapters = new HashMap<>();

	private ModuleManager() {
		if (!initialized) {
			loadModules();
			loadServices();
			initialized = true;
		}

	}

	public ArrayList<AbstractModule> getModules() {
		return modules;
	}

	/*
	Move to annotation based loading
	 */
	private void loadModules() {

		Reflections reflections;

		try {
			reflections = new Reflections("com.mysaasa");
		} catch (NoClassDefFoundError e) {
			throw new RuntimeException("Could not run Service Detector, SimpleGuicemoduleImpl", e);
		}

		registerAbstractModules(reflections);
		registerClassPanelAdapters();
	}

	private void registerAbstractModules(Reflections reflections) {
		for (Class c : reflections.getSubTypesOf(AbstractModule.class)) {
			try {
				modules.add((AbstractModule) c.newInstance());
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

	private void registerClassPanelAdapters() {
		for (AbstractModule module : modules) {
			Map<Class, IClassPanelAdapter> m = module.getClassPanelAdapters();
			if (m != null)
				for (Class c : m.keySet()) {
					if (classPanelAdapters.containsKey(c)) {
						throw new IllegalStateException("Two adapters for class " + c + m.getClass().getName());
					}
				}
			if (m != null)
				classPanelAdapters.putAll(m);
		}
	}

	//TODO use the DI system to calculate what to load
	private static void loadServices() {
		Collection<Binding<?>> bindings = Simple.getInstance().getInjector().getBindings().values();
		for (Binding b : bindings) {
			Object injectedObj = b.getProvider().get();
			if (injectedObj instanceof IApiService)
				ApiHelperService.get().bindApiService((IApiService) injectedObj);
			if (injectedObj instanceof ITemplateService)
				TemplateHelperService.get().bindTemplateService((ITemplateService) injectedObj);

			Simple.getInstance().getInjector().injectMembers(injectedObj);
		}
	}

	/**
	 * The Modules register panels to classes for editing, this is how it's looked up
	 * @param aClass class
	 * @return adapter
	 */
	public IClassPanelAdapter getClassPanelAdapter(Class aClass) {
		return classPanelAdapters.get(aClass);
	}
}
