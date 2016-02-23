package com.mysaasa.core.organization.panels;

import com.mysaasa.SimpleImpl;
import com.mysaasa.core.organization.messages.ManageOrganization;
import com.mysaasa.core.organization.model.Organization;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.html.panel.Panel;

public class OrganizationAdmin extends Panel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6881467492971355182L;

	public OrganizationAdmin(String id) {
		super(id);
		add(new MyOrganizationDataTable());
	}

	private static class MyOrganizationDataTable extends OrganizationDataTable {
		public MyOrganizationDataTable() {
			super("OrganizationDataTable", new OrganizationDataProvider());
		}

		@Override
		public void onRowClick(final AjaxRequestTarget target, Organization o) {
			target.getPage().send(SimpleImpl.get(), Broadcast.BREADTH, new MyManageOrganization(o, target));
		}

		private static class MyManageOrganization extends ManageOrganization {

			private final AjaxRequestTarget target;

			public MyManageOrganization(Organization o, AjaxRequestTarget target) {
				super(o);
				this.target = target;
			}

			@Override
			public AjaxRequestTarget getAjaxRequestTarget() {
				return target;
			}
		}
	}
}
