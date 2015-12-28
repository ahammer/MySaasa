package com.mysassa.simple.core.organization.messages;

import com.mysassa.simple.messages.AjaxMessage;
import com.mysassa.simple.core.organization.model.Organization;

/**
 * Request the User Edit ability, the Users Module should respond to this message by default
 *
 * Created by Adam on 2/18/14.
 */
public abstract class ManageOrganization extends AjaxMessage {
	private final Organization organization;

	public ManageOrganization(Organization u) {
		super();
		organization = u;
	}

	@Override
	public String toString() {
		return "ManageOrganization{" + "organization=" + organization + '}';
	}

	public Organization getOrganization() {
		return organization;
	}
}
