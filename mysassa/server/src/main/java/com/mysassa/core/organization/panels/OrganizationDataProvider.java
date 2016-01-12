package com.mysassa.core.organization.panels;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.mysassa.Simple;
import com.mysassa.core.organization.model.Organization;
import com.mysassa.core.organization.services.OrganizationService;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

public class OrganizationDataProvider extends SortableDataProvider<Organization, String> {
	private static final long serialVersionUID = 1L;

	public OrganizationDataProvider() {}

	@Override
	public Iterator<? extends Organization> iterator(long first, long count) {
		return OrganizationService.get().getAllOrganizations().subList((int) first, (int) (first + count)).iterator();
	}

	@Override
	public IModel<Organization> model(Organization object) {
		return new CompoundPropertyModel(object);
	}

	@Override
	public long size() {
		return OrganizationService.get().getAllOrganizations().size();
	}
}
