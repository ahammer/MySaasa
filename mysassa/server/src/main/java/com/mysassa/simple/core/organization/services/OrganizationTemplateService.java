package com.mysassa.simple.core.organization.services;

import com.mysassa.simple.interfaces.ITemplateService;

/**
 * Created by Adam on 4/3/14.
 */
public class OrganizationTemplateService implements ITemplateService {
	public OrganizationTemplateService() {}

	@Override
	public String getTemplateInterfaceName() {
		return "Org";
	}

}
