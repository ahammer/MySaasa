package com.mysassa.interfaces.templating;

import com.mysassa.interfaces.ITemplateService;

/**
 * This is the Abstract class for the Template Service
 *
 * Created by Adam on 3/15/14.
 */
public abstract class WebsiteTemplateService implements ITemplateService {
	public static final String TEMPLATE_SHORT_NAME = "Website";

	@Override
	public String getTemplateInterfaceName() {
		return TEMPLATE_SHORT_NAME;
	}

	//  Looks up the text bound to this content tag. If DebugMode is enabled
	//  The content will be wrapped in a Span and made editable.
	//  @Params The name of this ContentBinding
	//  @Returns the String Content RunContext, hiding behind the Content Binding.
	public abstract String bind(String name);

	public abstract String bind(String name, String defaultValue);

	public abstract String getAdminLink();

}
