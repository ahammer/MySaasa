package com.mysaasa.core.editor.panels;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mysaasa.core.website.panels.details.FileDetails;
import com.mysaasa.core.media.model.Media;
import com.mysaasa.core.security.services.SessionService;
import com.mysaasa.core.website.model.TemplateFile;
import com.mysaasa.core.website.panels.FileBrowser;
import com.mysaasa.messages.MessageHelpers;
import org.apache.commons.io.FileUtils;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.Session;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import java.io.*;

/**
 * The AcePanel is the Ace Editor and it's Affiliate Sidebar, Saving and Integration with the System.
 *
 * It's a bit different then the other
 * Created by Adam on 4/5/14.
 */
public class Ace extends Panel {
	private static final Gson gson = new GsonBuilder().disableHtmlEscaping().create();

	public Ace(String id, Model<File> model) {
		super(id, model);
		setOutputMarkupId(true);
		TemplateFile tf = new TemplateFile(model.getObject());
		add(new AceForm("aceForm", model));

	}

	@Override
	public MarkupContainer setDefaultModel(IModel<?> model) {
		super.setDefaultModel(model);
		return this;
	}

	public class AceForm extends Form {
		private final WebMarkupContainer editor;
		private final HiddenField hiddenValue;

		String value;

		public void setValue(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}

		@Override
		public void renderHead(IHeaderResponse response) {
			File file = (File) Ace.this.getDefaultModelObject();
			String content = null;
			String mode = "ace/mode/text";
			try {

				content = gson.toJson(FileUtils.readFileToString(file, "UTF8"));
				Media.Format format = Media.Format.fromFile(file);
				switch (format) {
				case HTML:
					mode = "ace/mode/html";
					break;
				case JS:
					mode = "ace/mode/javascript";
					break;
				case CSS:
					mode = "ace/mode/css";
					break;
				default:
					mode = "ace/mode/text";
					break;
				}
			} catch (IOException e) {
				content = "error";
				e.printStackTrace();
			}

			//3 Javascripts.
			//1 - Load Editor and Load File
			//2 - Listen for Events from Websocket (Request Save) (When this - Do the next thing Websocket in->Ajax out)
			//3 - Wicket Ajax Postback for saving

			//To re-iterate, we need to load contents into the editor, we need to be able to receieve websocket request to save
			//We also need to be able to post the results.
			final String initAce = "var editor=ace.edit(\"" + editor.getMarkupId() + "\");editor.setValue(" + content + ");editor.getSession().setMode(\"" + mode + "\");editor.getSelection().clearSelection();\n\n";
			response.render(OnDomReadyHeaderItem.forScript(initAce));

		}

		public AceForm(String id, final IModel<File> model) {
			super(id, model);
			add(new FileBrowser(new Model((new TemplateFile((File) Ace.this.getDefaultModelObject())).getWebsite())) {
				@Override
				protected void clickFile(AjaxRequestTarget target, File file) {
					MessageHelpers.editEventMessage(target, new Model(new File(file.getAbsolutePath())));

				}
			});
			add(editor = new WebMarkupContainer("editor"));
			add(hiddenValue = new HiddenField("value", new PropertyModel(this, "value")));

			TemplateFile file = new TemplateFile(model.getObject());
			add(new Label("production", file.getWebsite().getProduction()));
			add(new Label("environment", SessionService.get().getAdminSession(Session.get()).getEnv()));
			//Cancel and Return

			add(new FileDetails("fileDetails", new CompoundPropertyModel(model.getObject()), new Model(file.getWebsite())));

			add(new MyAjaxSubmitLink("submit") {
				@Override
				protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
					super.onSubmit(target, form);
					saveFile();
				}

			});
		}

		/**
		 * Get the path to the currently edited file, and save what is posted
		 * If it's a .scss file, let's compile as well.
		 */
		private void saveFile() {
			String path = Ace.this.getDefaultModelObjectAsString();
			try {
				FileUtils.writeStringToFile(new File(path), value);
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (path.endsWith(".scss")) {
				TemplateFile file = new TemplateFile(path);
				file.invokeCompass();
			}
		}

		private class MyAjaxSubmitLink extends AjaxSubmitLink {
			public MyAjaxSubmitLink(String id) {
				super(id);
			}

			@Override
			public void renderHead(IHeaderResponse response) {
				/**
				 * When you click THIS button (AjaxSubmitLink), it'll assign the ace editor content to a form field
				 * before post. Because ACE is a div and not a form component.
				 */
				final String clickAlert = "$('#" + getMarkupId() + "')" + ".click(" + "function()" + "{" + "$('#" + hiddenValue.getMarkupId() + "').val(ace.edit(\"" + editor.getMarkupId() + "\").getValue());" + "}" + ");";
				response.render(OnDomReadyHeaderItem.forScript(clickAlert));
			}
		}
	}

}
