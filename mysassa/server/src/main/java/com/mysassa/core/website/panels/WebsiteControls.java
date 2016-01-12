package com.mysassa.core.website.panels;

import com.mysassa.core.website.WebsiteModule;
import com.mysassa.core.website.model.Website;
import com.mysassa.messages.MessageHelpers;
import com.mysassa.core.hosting.service.HostingService;

import com.mysassa.core.security.services.SessionService;
import com.mysassa.core.security.services.session.AdminSession;
import com.mysassa.core.website.services.WebsiteService;
import com.mysassa.messages.ModuleClickedMessage;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class WebsiteControls extends Panel {
	private static final long serialVersionUID = 3834362763645810612L;
	private final DropDownChoice environmentSwitch;
	private final AjaxLink deployButton;
	String theme = "No Theme";

	public WebsiteControls(String id, final IModel<Website> model) {
		super(id, model);
		checkNotNull(model);
		checkNotNull(model.getObject());
		if (AdminSession.get().getTheme() != null)
			theme = AdminSession.get().getTheme().getProduction();
		setOutputMarkupId(true);
		environmentSwitch = new DropDownChoice("environment", new PropertyModel(AdminSession.get(), "env"),
				new LoadableDetachableModel<List<AdminSession.Environment>>() {
					@Override
					protected List<AdminSession.Environment> load() {
						return Arrays.asList(AdminSession.Environment.values());
					}
				});

		add(new AjaxCheckBox("editMode", new PropertyModel(AdminSession.get(), "editMode")) {
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				SessionService.get().notifyAdminSessionUpdate(target);
			}
		});

		add(new AjaxCheckBox("newPost", new PropertyModel(AdminSession.get(), "newPostAllowed")) {
			@Override
			protected void onUpdate(AjaxRequestTarget target) {

				SessionService.get().notifyAdminSessionUpdate(target);
			}

			@Override
			protected void onConfigure() {
				super.onConfigure();
				setEnabled(AdminSession.get().getEditMode());//Can only use in edit mode;
			}
		});

		add(deployButton = new AjaxLink("Deploy") {

			@Override
			public void onClick(final AjaxRequestTarget target) {
				AdminSession adminSession = AdminSession.get();
				Website website = (Website) WebsiteControls.this.getDefaultModelObject();

				WebsiteService.get().deployStaging(website);
				adminSession.setTheme(null);
				adminSession.setEnv(AdminSession.Environment.Live);
				target.add(WebsiteControls.this);
				new ModuleClickedMessage(new WebsiteModule(), new Model(website), target).send();

			}
		});

		//TODO upgrade theme from String to Website
		DropDownChoice<String> themeChoice = new DropDownChoice<String>("themes",
				new PropertyModel<String>(WebsiteControls.this, "theme"),
				new LoadableDetachableModel<List<String>>() {
					@Override
					protected List<String> load() {
						List<Website> website_list = Collections.EMPTY_LIST;//InventoryService.get().getThemesAsWebsites();
						String[] websites = new String[website_list.size() + 1];
						websites[0] = "No Selection";
						int i = 1;
						for (Website w : website_list) {
							websites[i] = w.getProduction();
							i++;
						}
						return Arrays.asList(websites);
					}
				});

		add(themeChoice);
		add(environmentSwitch);

		themeChoice.add(new ThemeSelected());
		environmentSwitch.add(new EnvironmentSelected());
	}

	@Override
	public void onConfigure() {
		super.onConfigure();
		deployButton.setVisible(AdminSession.get().getEnv() == AdminSession.Environment.Staging);
		environmentSwitch.setEnabled(AdminSession.get().getTheme() == null);
	}

	private class ThemeSelected extends AjaxFormComponentUpdatingBehavior {
		public ThemeSelected() {
			super("onchange");
		}

		@Override
		protected void onUpdate(final AjaxRequestTarget target) {

			Website t = HostingService.get().findWebsite(theme);
			AdminSession.get().setTheme(t);
			if (t == null) {
				AdminSession.get().setEnv(AdminSession.Environment.Live);
			}
			MessageHelpers.loadWebsiteEditor(target, (Website) WebsiteControls.this.getDefaultModelObject());
		}
	}

	/**
	 * This is for when the Environment Switch in the header is selected, it brings the form value over via ajax
	 * after a update
	 */
	private class EnvironmentSelected extends AjaxFormComponentUpdatingBehavior {
		public EnvironmentSelected() {
			super("onchange");
		}

		@Override
		protected void onUpdate(AjaxRequestTarget target) {
			//   SessionService.get().notifyAdminSessionUpdate(target);
			//   target.add(WebsiteControls.this);

			MessageHelpers.loadWebsiteEditor(target, (Website) WebsiteControls.this.getDefaultModelObject());
		}
	}
}
