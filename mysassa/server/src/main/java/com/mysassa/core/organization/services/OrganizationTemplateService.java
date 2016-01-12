package com.mysassa.core.organization.services;

import com.mysassa.interfaces.ITemplateService;

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
