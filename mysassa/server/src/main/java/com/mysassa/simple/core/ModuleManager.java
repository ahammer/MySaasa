package com.mysassa.simple.core;

import java.util.*;

import com.google.inject.Binding;
import com.mysassa.simple.Simple;
import com.mysassa.simple.api.ApiHelperService;
import com.mysassa.simple.core.categories.CategoriesModule;

import com.mysassa.simple.core.editor.EditorModule;
import com.mysassa.simple.core.help.HelpModule;

import com.mysassa.simple.core.messaging.MessagingModule;

import com.mysassa.simple.core.splash.SplashModule;
import com.mysassa.simple.interfaces.IClassPanelAdapter;
import com.mysassa.simple.interfaces.IApiService;
import com.mysassa.simple.interfaces.ITemplateService;
import com.mysassa.simple.core.blog.BlogModule;
import com.mysassa.simple.core.hosting.HostingModule;
import com.mysassa.simple.core.media.MediaModule;
import com.mysassa.simple.core.organization.OrganizationModule;
import com.mysassa.simple.core.security.SecurityModule;
import com.mysassa.simple.core.users.UsersModule;
import com.mysassa.simple.core.website.WebsiteModule;
import com.mysassa.simple.core.website.templating.TemplateHelperService;
import com.mysassa.simple.interfaces.annotations.SimpleService;
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
 * Templating uses injection to get at these classes, so TemplateServices need injection hooks.
 */
public class ModuleManager {

	private static ModuleManager INSTANCE;

	private static final ArrayList<AbstractModule> modules = new ArrayList<AbstractModule>();
	private static final HashMap<String, Object> services = new HashMap();
	private static volatile boolean initialized = false;
	private static final HashMap<Class, IClassPanelAdapter> classPanelAdapters = new HashMap<>();

	public ModuleManager() {
		if (!initialized) {
			loadModules();
			loadServices();
			initialized = true;
		}
		INSTANCE = this;
	}

	public static ModuleManager getInstance() {
		return INSTANCE;
	}

	public ArrayList<AbstractModule> getModules() {
		return modules;
	}

	/*
	Move to annotation based loading
	 */
	private static void loadModules() {

		Reflections reflections;

		try {
			reflections = new Reflections("com.mysassa");
		} catch (NoClassDefFoundError e) {
			throw new RuntimeException("Could not run Service Detector, SimpleGuicemoduleImpl", e);
		}

		Set<Class> bound = new HashSet<>();
		for (Class c : reflections.getSubTypesOf(AbstractModule.class)) {
			try {
				modules.add((AbstractModule) c.newInstance());
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		/*
		modules.add(new HostingModule());
		modules.add(new WebsiteModule());
		modules.add(new UsersModule());
		modules.add(new SecurityModule());
		modules.add(new BlogModule());
		modules.add(new MediaModule());
		modules.add(new OrganizationModule());
		modules.add(new EditorModule());
		//modules.add(new InventoryModule());
		//modules.add(new BitcoinModule());
		//modules.add(new OrdersModule());
		//modules.add(new DatabaseModule());
		modules.add(new HelpModule());
		modules.add(new SplashModule());
		modules.add(new MessagingModule());
		modules.add(new CategoriesModule());
		*/
		for (AbstractModule module : modules) {
			Map<Class, IClassPanelAdapter> m = module.getClassPanelAdapters();
			if (m != null)
				for (Class c : m.keySet()) {
					if (classPanelAdapters.containsKey(c)) {
						throw new IllegalStateException("Two adapters for class " + c);
					}
				}
			if (m != null)
				classPanelAdapters.putAll(m);
		}
	}

	static boolean service_loaded = false;

	//TODO use the DI system to calculate what to load
	private static void loadServices() {
		checkArgument(service_loaded == false);
		Collection<Binding<?>> bindings = Simple.get().getInjector().getBindings().values();
		for (Binding b : bindings) {
			Object injectedObj = b.getProvider().get();
			if (injectedObj instanceof IApiService)
				ApiHelperService.get().bindApiService((IApiService) injectedObj);
			if (injectedObj instanceof ITemplateService)
				TemplateHelperService.get().bindTemplateService((ITemplateService) injectedObj);
		}
		service_loaded = true;
	}

	/**
	 * The Modules register panels to classes for editing, this is how it's looked up
	 * @param aClass
	 * @return
	 */
	public IClassPanelAdapter getClassPanelAdapter(Class aClass) {
		return classPanelAdapters.get(aClass);
	}
}
