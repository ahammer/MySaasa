package com.mysaasa.injection;

import com.google.inject.Provides;
import org.slf4j.Logger;

import javax.persistence.EntityManager;

/**
 * Abstract Guice Module, shows what Dependency injection provides.
 * <p> Add new classes to this file if you want to provide things in the dependency injection.</p>
 * <p> However, the Core dependency injection and core should really be left alone, if you are creating
 * a external module, please create your own Injector for your classes.
 *
 * <p> I only use manual instance injection, because I do not like to retain services or</p>
 *
 */
public abstract class AbstractSimpleGuiceModule extends com.google.inject.AbstractModule {

}
