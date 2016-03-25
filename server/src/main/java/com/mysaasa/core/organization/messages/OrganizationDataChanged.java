package com.mysaasa.core.organization.messages;

import com.mysaasa.messages.AjaxMessage;
import com.mysaasa.core.organization.model.Organization;

public abstract class OrganizationDataChanged extends AjaxMessage {

	public abstract static class OrganizationDeleted extends OrganizationDataChanged {
		public OrganizationDeleted(Organization u) {
			super(u);
		}
	}

	public abstract static class OrganizationUpdated extends OrganizationDataChanged {
		public OrganizationUpdated(Organization u) {
			super(u);
		}
	}

	private final Organization organization;

	OrganizationDataChanged(Organization u) {
		super();
		organization = u;
	}

}
