package com.mysassa.simple.core.hosting.panels.manager;

import com.mysassa.simple.Simple;
import com.mysassa.simple.core.organization.model.Organization;
import com.mysassa.simple.core.organization.services.OrganizationService;
import com.mysassa.simple.core.security.services.session.SecurityContext;
import com.mysassa.simple.core.users.model.User;
import com.mysassa.simple.core.website.model.Domain;
import com.mysassa.simple.core.website.model.Website;
import com.mysassa.simple.ui.multi_select.ArraySelectWidget;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class HostingManagement extends Panel {
	private static final long serialVersionUID = 1L;
	private final HostingForm form;

	public class HostingForm extends Form {
		public static final long serialVersionUID = 1L;
		public final TextField<String> domain;
		final FeedbackPanel feedback;
		private final WebMarkupContainer newLabel;
		private final WebMarkupContainer editLabel;
		private final ExternalLink goStaging;
		private final ExternalLink goProduction;
		private final DeleteLink deleteLink;
		ArrayList<String> domains = new ArrayList();

		@Override
		protected void onConfigure() {
			super.onConfigure();
			Website website = (Website) getModelObject();
			editLabel.setVisible(website.getId() != 0);
			deleteLink.setVisible(website.getId() != 0);
			newLabel.setVisible(website.getId() == 0);
			goStaging.setVisible(website.getId() != 0 && website.getStaging() != null);
			goProduction.setVisible(website.getId() != 0 && website.getProduction() != null);
			if (SecurityContext.get().getUser().getAccessLevel() != User.AccessLevel.ROOT) {
				domain.setEnabled(false);
				goStaging.setEnabled(false);
			}
		}

		public HostingForm(CompoundPropertyModel<Website> model) {
			super("websiteForm", model);
			setOutputMarkupId(true);
			final Website website = model.getObject();
			for (Domain d : website.getDomains())
				domains.add(d.domain);
			add(editLabel = new WebMarkupContainer("EditLabel"));
			add(newLabel = new WebMarkupContainer("NewLabel"));
			add(domain = new TextField<>("production"));

			add(new CheckBox("isVisible"));
			add(goProduction = new ExternalLink("goProduction", new Model("http://" + website.production + ":" + Simple.getPort())));
			add(goStaging = new ExternalLink("goStaging", new Model("http://" + website.staging + ":" + Simple.getPort())));
			add(new DomainSelectWidget());
			add(new TextField<String>("staging"));
			add(feedback = new FeedbackPanel("feedback"));
			feedback.setOutputMarkupId(true);
			//TODO upgrade website from String to Website
			DropDownChoice<Organization> organization = new DropDownChoice<Organization>("organization",
					new PropertyModel<Organization>(HostingForm.this.getDefaultModelObject(), "organization"),
					new LoadableDetachableModel<List<Organization>>() {
						@Override
						protected List<Organization> load() {
							List<Organization> website_list = OrganizationService.get().getAllOrganizations();
							return website_list;
						}

					}) {

			};
			add(organization);
			organization.setRequired(true);
			add((SubmitLink) new SubmitLink(HostingManagement.this));
			add(deleteLink = new DeleteLink(HostingManagement.this));
			domain.setRequired(true);
		}

		private class DomainSelectWidget extends ArraySelectWidget<String> {
			public DomainSelectWidget() {
				super("domains", new Model(HostingForm.this.domains), new Model("Additional Domain"), new Model("Add"));
			}

			@Override
			public Iterator<String> getChoices() {
				return domains.iterator();
			}

			@Override
			public String fromString(String string) {
				return string;
			}

			@Override
			protected String convertToString(String object) {
				return object;
			}

			@Override
			public void populateListItem(final ListItem item, boolean selected) {
				class MyWebMarkupContainer extends WebMarkupContainer {
					public MyWebMarkupContainer() {
						super("wrapper");
						add(new Label("name", item.getModel()));
						add(new AjaxLink("remove") {
							@Override
							public void onClick(AjaxRequestTarget target) {
								String s = (String) item.getModelObject();
								domains.remove(s);
								refresh(target);
							}
						});
					}
				}
				item.add(new MyWebMarkupContainer());

			}

		}
	}

	public HostingManagement(String id, CompoundPropertyModel<Website> model) {
		super(id, model);
		setOutputMarkupId(true);
		add(form = new HostingForm(model));
	}

	public HostingForm getForm() {
		return form;
	}

}
