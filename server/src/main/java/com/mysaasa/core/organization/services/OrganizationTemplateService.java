package com.mysaasa.core.organization.services;

import com.mysaasa.interfaces.ITemplateService;

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
