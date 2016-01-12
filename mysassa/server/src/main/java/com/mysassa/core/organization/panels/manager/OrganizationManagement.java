package com.mysassa.core.organization.panels.manager;

import com.mysassa.core.organization.model.Organization;
import com.mysassa.core.organization.services.OrganizationService;
import com.mysassa.core.users.model.ContactInfo;
import com.mysassa.messages.MessageHelpers;
import com.mysassa.core.users.panels.ContactInfoEditor;
import com.mysassa.core.users.panels.VerifyAdmin;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.AjaxEditableLabel;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

public class OrganizationManagement extends Panel {
	private static final long serialVersionUID = 1L;
	private final WebMarkupContainer labelNew;
	private final WebMarkupContainer labelEdit;
	private final FeedbackPanel feedback;
	private final ModalWindow modal;

	public OrganizationManagement(String id, final CompoundPropertyModel<Organization> model) {
		super(id, model);
		setOutputMarkupId(true);
		add(labelEdit = new WebMarkupContainer("EditLabel"));
		add(labelNew = new WebMarkupContainer("NewLabel"));
		add(modal = new ModalWindow("modal"));
		add(new AjaxEditableLabel("name") {
			@Override
			protected void onSubmit(AjaxRequestTarget target) {
				super.onSubmit(target);
				target.add(OrganizationManagement.this);
				persistModel(target);
			}

			@Override
			protected void onError(AjaxRequestTarget target) {
				super.onError(target);
				target.add(OrganizationManagement.this);
			}
		});

		add(new AjaxEditableLabel("stripeKey") {
			@Override
			protected void onSubmit(AjaxRequestTarget target) {
				super.onSubmit(target);
				target.add(OrganizationManagement.this);
				persistModel(target);
			}

			@Override
			protected void onError(AjaxRequestTarget target) {
				super.onError(target);
				target.add(OrganizationManagement.this);
			}
		});

		/*
		add(linkSubscription = new AjaxLink("subscription") {
			@Override
			public void onClick(AjaxRequestTarget target) {
				//MessageHelpers.editEventMessage(target, new CompoundPropertyModel<Cart>(model.getObject().getSubscription()));
			}
		});*/

		//add(wallet = new OrganizationWalletManager("wallet", model));
		add(feedback = new FeedbackPanel("feedback"));
		add(new ContactInfoEditor("contactInfo", new CompoundPropertyModel<ContactInfo>(model.getObject().getContactInfo())) {
			@Override
			public void updated(AjaxRequestTarget target, IModel model) {
				persistModel(target);
			}
		});
		add(new AjaxLink("deleteLink") {
			@Override
			public void onClick(AjaxRequestTarget target) {

				try {
					modal.setTitle("Are you sure you want to Delete?");
					modal.setContent(new VerifyAdmin(modal.getContentId()) {
						@Override
						protected void grantAccessTemporarily(AjaxRequestTarget target) {
							Organization o = model.getObject();
							OrganizationService.get().disableOrganization(o);
							OrganizationManagement.this.setDefaultModelObject(new Organization());
							MessageHelpers.notifyUpdate(target, o);
							target.add(OrganizationManagement.this);
							modal.close(target);
						}

					});
					modal.show(target);

				} catch (IllegalStateException e) {
					e.printStackTrace();
					error("Can not delete this organization without first deleting Users, Products, Blogposts");
					target.add(OrganizationManagement.this);
				}
			}
		});

	}

	@Override
	protected void onConfigure() {
		super.onConfigure();
		Organization o = (Organization) getDefaultModelObject();
		labelEdit.setVisible(o.getId() != 0);
		labelNew.setVisible(o.getId() == 0);
		//linkSubscription.setVisible((o.getSubscription() != null));
		//wallet.setVisible(o.getId() != 0);

	}

	private void persistModel(AjaxRequestTarget target) {
		Organization org = (Organization) OrganizationManagement.this.getDefaultModelObject();
		if (!organizationReadyToCreate(org)) {
			error("Email and ID Required");
		} else {
			if (org.id == 0) {
				info("Created organization");
			} else {
				info("Saved organization");
			}
			this.setDefaultModelObject(org = OrganizationService.get().saveOrganization(org));
			MessageHelpers.notifyUpdate(target, org);
			target.add(this);
		}

	}

	private boolean organizationReadyToCreate(Organization organization) {

		boolean hasIdentifier = organization.getName() != null && organization.getName().length() > 3;
		boolean result = hasIdentifier;
		return result;

	}

}
