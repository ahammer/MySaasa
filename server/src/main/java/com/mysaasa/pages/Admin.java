package com.mysaasa.pages;

import com.mysaasa.core.splash.SplashModule;
import com.mysaasa.core.users.model.User;
import com.mysaasa.core.website.model.Website;
import com.mysaasa.core.messaging.model.Message;
import com.mysaasa.core.messaging.panels.MessagePanel;
import com.mysaasa.core.messaging.services.MessagingService;
import com.mysaasa.core.security.services.SessionService;
import com.mysaasa.core.security.services.session.SecurityContext;
import com.mysaasa.core.users.service.UserService;
import com.mysaasa.messages.MessageHelpers;
import com.mysaasa.ui.ActiveUserInfo;
import com.mysaasa.ui.StatusPanel;
import com.mysaasa.ui.content.LocalEditorPanel;
import com.mysaasa.ui.content.RegionalContainer;
import com.mysaasa.ui.content.PanelContainer;
import com.mysaasa.ui.menu.AbstractModuleLink;
import com.mysaasa.core.AbstractModule;
import com.mysaasa.messages.ModuleClickedMessage;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.*;
import org.apache.wicket.protocol.https.RequireHttps;
import org.apache.wicket.protocol.ws.api.WebSocketBehavior;
import org.apache.wicket.protocol.ws.api.WebSocketRequestHandler;
import org.apache.wicket.protocol.ws.api.message.ClosedMessage;
import org.apache.wicket.protocol.ws.api.message.ConnectedMessage;
import org.apache.wicket.protocol.ws.api.message.IWebSocketPushMessage;

import java.util.List;

@RequireHttps
public class Admin extends WebPage implements IHeaderContributor, AdminInterface {
	private static final long serialVersionUID = 1L;
	private final PanelContainer mainSection;
	private final RepeatingView headerButtons;
	private final Label organization;
	private final ModalWindow sendMessageModal;

	private final AjaxLink sendMessage;
	private AbstractModule lastClicked;
	private Website selectedWebsite;

	public Admin() {
		super();
		setWasCreatedBookmarkable(false);

		Website initialWebsite = null;
		SecurityContext securityContext = SecurityContext.get();
		DropDownChoice<Website> websiteLabel;

		if (securityContext == null) {
			setResponsePage(Splash.class);
			mainSection = null;
			headerButtons = null;
			organization = null;
			sendMessageModal = null;
			sendMessage = null;
			return;
		}

		List<Website> website_list = securityContext.getWebsites();
		website_list.sort((o1, o2) -> {
			if (o1.production == null)
				return 1;
			if (o2.production == null)
				return -1;
			return o1.production.compareTo(o2.production);
		} );
		if (website_list != null && website_list.size() > 0) {
			initialWebsite = website_list.get(0);
		}

		selectedWebsite = initialWebsite;

		add(new AjaxLink("logo") {
			@Override
			public void onClick(AjaxRequestTarget target) {
				new ModuleClickedMessage(new SplashModule(), target).send();
			}
		});

		add(new StatusPanel("status"));
		add(new ActiveUserInfo(new CompoundPropertyModel<User>(securityContext.getUser())));
		add(mainSection = new RegionalContainer());
		add(organization = new Label("organization", new Model(securityContext.getUser().getOrganization())));
		add(sendMessageModal = new ModalWindow("sendMessageModal"));
		add(sendMessage = new AjaxLink("sendMessage") {
			@Override
			public void onClick(AjaxRequestTarget target) {
				sendMessageModal.setTitle("Contact Support");
				Message m = MessagingService.get().getSupportThread();
				sendMessageModal.setContent(new MessagePanel(sendMessageModal.getContentId(), m));
				//sendMessageModal.setContent(new Label(sendMessageModal.getContentId(), "blah"));
				sendMessageModal.show(target);
			}
		});

		add(new AjaxLink("go") {
			@Override
			public void onClick(AjaxRequestTarget target) {
				MessageHelpers.loadWebsiteEditor(target, selectedWebsite);
			}
		});
		add(websiteLabel = new DropDownChoice<Website>("website",
				new PropertyModel(this, "selectedWebsite"),
				website_list, new IChoiceRenderer<Website>() {
			@Override
			public Object getDisplayValue(Website object) {
				return object.getProduction();
			}

			@Override
			public String getIdValue(Website object, int index) {
				return String.valueOf(object.getId());
			}

			@Override
			public Website getObject(String id, IModel<? extends List<? extends Website>> choices) {
				//TODO this cod
				List<? extends Website> objects = choices.getObject();
				for (Website w: objects) {
					if (w.getId() == Integer.valueOf(id)) return w;
				}
				return null;
			}
		}));

		if (securityContext.getUser().accessLevel == User.AccessLevel.ROOT || securityContext.getUser().accessLevel == User.AccessLevel.ORG) {
			organization.add(new AjaxEventBehavior("click") {
				@Override
				protected void onEvent(AjaxRequestTarget target) {
					MessageHelpers.editEventMessage(target, organization.getDefaultModel());
				}
			});

		} else {
			organization.setEnabled(false);
		}

		websiteLabel.add(new AjaxFormComponentUpdatingBehavior("change") {
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				if (selectedWebsite != null)
					MessageHelpers.loadWebsiteEditor(target, selectedWebsite);

			}
		});

		add(new LocalEditorPanel());

		headerButtons = new RepeatingView("moduleMenu");
		SecurityContext c = SessionService.get().getSecurityContext(getSession());
		for (final AbstractModule module : c.getAvailableModules()) {
			if (module.getMenuTitle() != null) {
				headerButtons.add(new MyAbstractModuleLink(headerButtons.newChildId(), module));
			}
		}

		add(headerButtons);

		if (initialWebsite != null) {
			MessageHelpers.loadWebsiteEditor(initialWebsite, this);
		}

		add(new WebSocketBehavior() {
			@Override
			protected void onPush(WebSocketRequestHandler handler, IWebSocketPushMessage message) {
				super.onPush(handler, message);
				MessageHelpers.broadcastPushEvent(Admin.this, handler, message);

			}

			@Override
			protected void onConnect(ConnectedMessage message) {
				super.onConnect(message);
				UserService.get().RegisterUserWebsocket(SecurityContext.get().getUser(), message.getKey(), message.getSessionId());

			}

			@Override
			protected void onClose(ClosedMessage message) {
				super.onClose(message);
				UserService.get().UnregisterUserWebsocket(SecurityContext.get().getUser(), message.getKey(), message.getSessionId());
			}
		});

		if (SecurityContext.get().getUser().accessLevel == User.AccessLevel.GUEST) {
			MessageHelpers.editEventMessage(Admin.this, new Model(SecurityContext.get().getUser()));
		}

	}

	@Override
	public PanelContainer getMainSection() {
		return mainSection;
	}

	@Override
	public RepeatingView getHeaderButtons() {
		return headerButtons;
	}

	private class MyAbstractModuleLink extends AbstractModuleLink {

		private final AbstractModule module;

		public String getSelectedProperty() {

			if (module == lastClicked) {
				return "Selected";
			}
			return "";
		}

		public MyAbstractModuleLink(String id, AbstractModule module) {
			super(id, new Model(module));
			this.module = module;
			add(new AttributeAppender("class", new PropertyModel(this, "selectedProperty"), " "));
		}

		@Override
		public void clicked() {
			lastClicked = module;
		}

	}

	public Website getSelectedWebsite() {
		return selectedWebsite;
	}

	public void setSelectedWebsite(Website selectedWebsite) {
		this.selectedWebsite = selectedWebsite;
	}

}
