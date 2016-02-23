package com.mysaasa.interfaces.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * This annotation is for the Dynamic loader to say "Hey, I need to be loaded"
 *
 * Initially it supported mocking, but I dropped it because I don't mock anything anymore, I run on a mock/temporary
 * database with internal H2. Pretty fast, Pretty accurate, I don't think I'll switch.
 *
 * If you create a new ITemplateService/IApiService or just a ISimpleService you want injectable, mark it with this
 * and it will be processed and used by the System
 *
 * Created by Adam on 3/15/14.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface SimpleService {
	public static enum ImplementationScope {
		Live
	};

	public ImplementationScope RunContext() default ImplementationScope.Live;
}
