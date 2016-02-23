package com.mysaasa.core.editor.panels;

import com.mysaasa.core.website.WebsiteModule;
import com.mysaasa.core.website.messages.WebsiteFileClicked;
import com.mysaasa.core.website.model.TemplateFile;
import com.mysaasa.core.website.panels.details.EXTRAS;
import com.mysaasa.messages.ACTIONS;
import com.mysaasa.messages.AjaxIntent;
import com.mysaasa.messages.ModuleClickedMessage;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 * This is the header for the editor, basic functions are save and revert, so I'm implementing them first.
 *
 * They will send Intents to save or revert a file, with the path set to the current model.
 *
 * model is set by the Edit File event.
 *
 *
 * Created by administrator on 2014-05-24.
 */
public class EditorHeader extends Panel {
	public EditorHeader(String id, IModel<?> model) {
		super(id, model);
		setOutputMarkupId(true);
		add(new AjaxLink("save") {

			@Override
			public void onClick(AjaxRequestTarget target) {
				AjaxIntent i = new AjaxIntent(target);
				i.setAction(ACTIONS.ACTION_REQUEST_SAVE);
				i.getExtras().put(EXTRAS.FILE_PATH, (String) EditorHeader.this.getDefaultModelObject());
				i.send(getPage());
			}
		});

		add(new AjaxLink("preview") {

			@Override
			public void onClick(final AjaxRequestTarget target) {
				TemplateFile file = new TemplateFile((String) EditorHeader.this.getDefaultModelObject());
				new ModuleClickedMessage(new WebsiteModule(), new Model(file.getWebsite()), target).send();
				new WebsiteFileClicked(target, file.getWebsite(), file).send();
			}
		});
	}
}
