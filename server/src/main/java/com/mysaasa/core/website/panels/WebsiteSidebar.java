package com.mysaasa.core.website.panels;

import com.mysaasa.core.blog.model.BlogPost;
import com.mysaasa.core.website.model.Website;
import com.mysaasa.core.website.panels.details.FileDetails;
import com.mysaasa.SimpleImpl;
import com.mysaasa.core.blog.messages.BlogPostModifiedMessage;
import com.mysaasa.core.blog.services.BlogService;
import com.mysaasa.core.help.panels.HelpPanel;
import com.mysaasa.core.hosting.service.HostingService;
import com.mysaasa.core.organization.model.Organization;
import com.mysaasa.core.security.services.SessionService;
import com.mysaasa.core.security.services.session.AdminSession;
import com.mysaasa.core.website.model.TemplateFile;
import com.mysaasa.core.website.services.WebsiteService;
import com.mysaasa.interfaces.templating.BlogTemplateService;
import com.mysaasa.messages.ACTIONS;
import com.mysaasa.messages.AjaxIntent;
import com.mysaasa.messages.MessageHelpers;
import com.mysaasa.core.website.model.ContentBinding;
import org.apache.wicket.Session;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.json.JSONArray;
import org.apache.wicket.ajax.json.JSONException;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.IRequestParameters;
import org.apache.wicket.request.Url;

import java.io.File;

import static com.google.common.base.Preconditions.checkNotNull;

public class WebsiteSidebar extends Panel {
	private static final long serialVersionUID = -9131534556975104206L;
	private final AjaxLink templateOptions;
	private final FileDetails details;
	private final FileBrowser fileBrowser;
	private final IModel<TemplateFile> fileModel;

	@Override
	public void renderHead(IHeaderResponse response) {
		//        if(ajaxInlineEditorCallback == null) throw new NullPointerException();
		String callbackUrl = ajaxInlineEditorCallback.getCallbackUrl().toString();

		/*
		SendMessageToAdmin
		    IN: msg (string), origin (source from browser)
		    Sends a message to the callback Url of the behaviour.
		    It sends both the msg and the origin.
		
		    Output: Async call to send the update.
		*/

		response.render(JavaScriptHeaderItem.forScript(BlogTemplateService.callbackFunction.replace("%callbackUrl%", callbackUrl), "MessageHook"));
	}

	public WebsiteSidebar(String id, final IModel<Website> model, final IModel<TemplateFile> fileModel) {
		super(id, model);
		this.fileModel = fileModel;
		setOutputMarkupId(true);
		add(new HelpPanel("help", HelpPanel.Sections.WebsiteSidebar));

		add(fileBrowser = new FileBrowser(model) {
			@Override
			protected void clickFile(AjaxRequestTarget target, File file) {
				MessageHelpers.editEventMessage(target, new Model(new TemplateFile(file)));
			}
		});
		add(new WebsiteControls("website_controls", model));
		add(new Label("environment", SessionService.get().getAdminSession(Session.get()).getEnv()));
		add(new Label("production", model.getObject().getProduction()).add(new AjaxEventBehavior("click") {
			@Override
			protected void onEvent(AjaxRequestTarget target) {
				MessageHelpers.editEventMessage(target, model);
			}
		}));
		add(templateOptions = new AjaxLink("templateOptions", new Model("Test")) {
			@Override
			public void onClick(AjaxRequestTarget target) {
				//Install this template and then refresh the panel, clear the theme selection
				AdminSession adminSession = AdminSession.get();
				WebsiteService.get().installTemplateIntoWebsiteStaging(adminSession.getTheme(), (Website) WebsiteSidebar.this.getDefaultModelObject());
				adminSession.setTheme(null);
				adminSession.setEnv(AdminSession.Environment.Staging);
				MessageHelpers.loadWebsiteEditor(target, model.getObject());
			}
		});

		if (fileBrowser == null) {
			add(new Label("fileBrowser", new Model<String>("User has no website access")));
		}

		add(details = new FileDetails("details", new CompoundPropertyModel<File>(fileModel.getObject()), model));

		addEditorCallbackAjaxBehaviour();
	}

	/**
	 * Hide the menu sections when stuff isn't correct
	 */
	@Override
	protected void onConfigure() {
		super.onConfigure();
		fileBrowser.setVisible((AdminSession.get().getTheme() == null));
		details.setVisible((AdminSession.get().getTheme() == null));
		templateOptions.setVisible((AdminSession.get().getTheme() != null));
	}

	/**
	 *
	 * @param event
	 */
	@Override
	public void onEvent(IEvent event) {
		if (event.getPayload() instanceof AjaxIntent) {
			AjaxIntent intent = (AjaxIntent) event.getPayload();
			if (intent.getAction().equals(ACTIONS.ACTION_USER_PREFS_UPDATED)) {
				intent.getAjaxRequestTarget().add(this);
			}
		}
	}

	private AbstractDefaultAjaxBehavior ajaxInlineEditorCallback;

	private enum BlogButtonMethod {
		save, publish, delete, edit, load, nil;

		static BlogButtonMethod lookup(String name) {
			try {
				return BlogButtonMethod.valueOf(name);
			} catch (Exception e) {
				return nil;
			}
		}
	}

	private void addEditorCallbackAjaxBehaviour() {
		ajaxInlineEditorCallback = new AbstractDefaultAjaxBehavior() {

			protected void respond(final AjaxRequestTarget target) {
				IRequestParameters postParameters = getRequest().getPostParameters();
				String msg = postParameters.getParameterValue("msg").toString();
				String origin = postParameters.getParameterValue("origin").toString();
				String id = postParameters.getParameterValue("id").toString();
				String method = postParameters.getParameterValue("method").toString();
				String title = postParameters.getParameterValue("title").toString();
				String summary = postParameters.getParameterValue("summary").toString();
				String subtitle = postParameters.getParameterValue("subtitle").toString();
				String json = postParameters.getParameterValue("json").toString();

				if (id.startsWith("http")) {
					//This is when you click something in the iframe, it updates the FileDetails on the left
					System.out.println("Path: " + id);
					Url url = Url.parse(id);
					Website w = HostingService.get().findWebsite(url);
					TemplateFile f = AdminSession.get().getEnv() == AdminSession.Environment.Live ? w.calculateProductionFile(url) : w.calculateStagingFile(url);

					AjaxIntent i = new AjaxIntent(target);
					i.setAction(ACTIONS.ACTION_WEBSITE_IFRAME_PAGELOAD);
					i.getExtras().put("file", f.getAbsolutePath());
					i.send();
				} else if (id.startsWith("BlogPostTitle")) {
					switch (BlogButtonMethod.lookup(method)) {
					case save:

						inlineSaveBlogPost(target, title, subtitle, summary, msg, id, json, false);
						break;
					case publish:
						inlineSaveBlogPost(target, title, subtitle, summary, msg, id, json, true);
						break;
					case delete:
						inlineDeleteBlogPost(target, id);
						break;
					case edit:
						editBlogPost(target, id);
						break;

					}
				} else {
					switch (BlogButtonMethod.lookup(method)) {
					case save:
						updateWebsiteContent(msg, origin, id);
						break;
					case delete:
						deleteWebsiteContent(target, msg, origin, id);
						break;
					}
				}
			}

		};
		add(ajaxInlineEditorCallback);
	}

	private void editBlogPost(final AjaxRequestTarget target, String id) {
		BlogService service = BlogService.get();
		//if (1==1)throw new RuntimeException(id);
		Organization org = ((Website) WebsiteSidebar.this.getDefaultModelObject()).getOrganization();
		Long _id = Long.parseLong(id.split("_ID_")[1]); //Same ID but as a long
		BlogPost post = null;
		if (_id != 0) {
			post = service.getBlogPostById(_id);
		}

		checkNotNull(post);
		MessageHelpers.editEventMessage(target, new Model(post));

	}

	private void inlineSaveBlogPost(final AjaxRequestTarget target, String title, String subtitle, String summary, String msg, String id, String json, boolean publish) {
		BlogService service = BlogService.get();
		Organization org = ((Website) WebsiteSidebar.this.getDefaultModelObject()).getOrganization();
		Long _id = Long.parseLong(id.split("_ID_")[1]); //Same ID but as a long
		BlogPost post;
		if (_id != 0) {
			post = service.getBlogPostById(_id);
		} else {
			post = new BlogPost(org);
		}

		if (subtitle != null && !subtitle.trim().equals(""))
			post.setSubtitle(subtitle);
		if (summary != null && !summary.trim().equals(""))
			post.setSummary(summary);
		if (msg != null && !msg.trim().equals(""))
			post.setBody(msg);
		if (title != null && !title.trim().equals(""))
			post.setTitle(title);

		if (publish)
			post.setPublished(!post.getPublished());

		try {
			JSONArray object = new JSONArray(json);
			for (int i = 0; i < object.length(); i++)
				post.addCategory(object.getString(i));

		} catch (JSONException e) {

		}

		service.saveBlogPost(post);
		if (_id == 0 || publish) {
			//If this is new, send the broadcast that it was saved/updated.
			//Normal inline editing is WYSIWYG so no update necessary, but after created a new post we should refresh
			//so User can create another
			send(SimpleImpl.get(), Broadcast.BREADTH, new BlogPostModifiedMessage() {
				@Override
				public AjaxRequestTarget getAjaxRequestTarget() {
					return target;
				}
			});
		}
	}

	private void inlineDeleteBlogPost(final AjaxRequestTarget target, String id) {
		BlogService service = BlogService.get();
		Long _id = Long.parseLong(id.split("_ID_")[1]);
		service.deleteBlogPost(service.getBlogPostById(_id));
		send(SimpleImpl.get(), Broadcast.BREADTH, new BlogPostModifiedMessage() {
			@Override
			public AjaxRequestTarget getAjaxRequestTarget() {
				return target;
			}
		});
	}

	private void updateWebsiteContent(String msg, String origin, String id) {
		WebsiteService websiteDataService = (WebsiteService) WebsiteService.get();
		HostingService HostingServiceImpl = HostingService.get();
		Website website = HostingServiceImpl.findWebsite(Url.parse(origin));
		ContentBinding b = websiteDataService.findBinding(id, website, id);
		b.getContent().setBody(msg);
		websiteDataService.saveContentBinding(b);
	}

	private void deleteWebsiteContent(final AjaxRequestTarget target, String msg, String origin, String id) {

		WebsiteService websiteDataService = (WebsiteService) WebsiteService.get();
		HostingService HostingServiceImpl = HostingService.get();
		Website website = HostingServiceImpl.findWebsite(Url.parse(origin));
		ContentBinding b = websiteDataService.findBinding(id, website, id);
		b.getContent().setBody(msg);
		websiteDataService.deleteContentBinding(b);
		send(SimpleImpl.get(), Broadcast.BREADTH, new BlogPostModifiedMessage() {
			@Override
			public AjaxRequestTarget getAjaxRequestTarget() {
				return target;
			}
		});

	}

}
