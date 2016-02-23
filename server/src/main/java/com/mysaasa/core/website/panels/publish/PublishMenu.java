package com.mysaasa.core.website.panels.publish;

import com.mysaasa.core.website.model.Website;
import org.apache.commons.io.FileUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import java.io.File;
import java.io.IOException;

/**
 * Created by administrator on 2014-06-04.
 */
public abstract class PublishMenu extends Panel {
	String changed = "";
	String added = "";

	public PublishMenu(String id, IModel<Website> website) {
		super(id, website);
		add(new Label("changed", new PropertyModel(this, "changed")));
		add(new Label("added", new PropertyModel(this, "added")));
		add(new AjaxLink("deploy") {
			@Override
			public void onClick(AjaxRequestTarget target) {
				Website w = (Website) PublishMenu.this.getDefaultModelObject();
				File websiteRoot = w.calculateProductionRoot();
				File stagingRoot = w.calculateStagingRoot();
				try {
					FileUtils.copyDirectory(stagingRoot, websiteRoot);
					FileUtils.deleteDirectory(stagingRoot);
					stagingRoot.mkdir();

				} catch (IOException e) {
					e.printStackTrace();
				}
				done(target);
			}
		});
		buildFileCache();
	}

	private void buildFileCache() {
		Website w = (Website) getDefaultModelObject();
		File websiteRoot = w.calculateProductionRoot();
		File stagingRoot = w.calculateStagingRoot();
		for (File f : stagingRoot.listFiles()) {
			changed += f.getName() + "\n";
		}

		changed += "\n";
	}

	public abstract void done(AjaxRequestTarget target);
}
