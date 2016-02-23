package com.mysaasa.core.website.panels;

import java.io.File;

import com.mysaasa.core.website.model.TemplateFile;
import com.mysaasa.core.website.model.Website;
import com.mysaasa.messages.MessageHelpers;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.PackageResourceReference;

/*
 * The individual elements in the file tree needs views, this will be a quick view of them
 */

public class FileTreeItem extends Panel {
	/**
	 *
	 */
	private static final long serialVersionUID = 597610208898108927L;
	private final AjaxLink edit;
	private final AjaxLink view;
	private final AjaxLink clone;
	private final AjaxLink remove;
	private final ModalWindow modal;
	private File file;
	private final Website website;
	private static final PackageResourceReference iconFolder = new PackageResourceReference(FileBrowser.class, "icon_folder.png");
	private static final PackageResourceReference iconCss = new PackageResourceReference(FileBrowser.class, "icon_css.png");
	private static final PackageResourceReference iconJs = new PackageResourceReference(FileBrowser.class, "icon_js.png");
	private static final PackageResourceReference iconJpeg = new PackageResourceReference(FileBrowser.class, "icon_jpeg.png");
	private static final PackageResourceReference iconPng = new PackageResourceReference(FileBrowser.class, "icon_png.png");
	private static final PackageResourceReference iconHtml = new PackageResourceReference(FileBrowser.class, "icon_html.png");
	private static final PackageResourceReference iconUnknown = new PackageResourceReference(FileBrowser.class, "icon_unknown.png");

	public FileTreeItem(String id, final IModel<File> model, final IModel<Website> websiteModel) {
		super(id, model);
		file = model.getObject();
		website = websiteModel.getObject();
		setOutputMarkupId(true);
		add(modal = new ModalWindow("modal"));

		add(edit = new AjaxLink("edit") {
			@Override
			public void onClick(AjaxRequestTarget target) {
				MessageHelpers.editEventMessage(target, model);
			}
		});
		add(view = new AjaxLink("view") {
			@Override
			public void onClick(AjaxRequestTarget target) {
				TemplateFile file = new TemplateFile(model.getObject());
				MessageHelpers.loadWebsiteEditor(target, websiteModel.getObject(), file);
			}
		});

		add(clone = new AjaxLink("clone") {
			@Override
			public void onClick(AjaxRequestTarget target) {
				MessageHelpers.loadWebsiteEditor(target, websiteModel.getObject(), new TemplateFile(model.getObject()));

			}
		});
		add(remove = new AjaxLink("remove") {
			@Override
			public void onClick(final AjaxRequestTarget target) {
				final File f = model.getObject();
				modal.setTitle("Delete this file?");
				modal.setContent(new DeleteFileConfirmation(modal.getContentId(), model) {
					@Override
					protected void yes(AjaxRequestTarget target) {
						f.delete();
						FileTreeItem.this.setVisible(false);
						target.add(FileTreeItem.this);
						modal.close(target);
					}

					@Override
					protected void no(AjaxRequestTarget target) {
						modal.close(target);

					}
				});
				modal.setInitialWidth(400);
				modal.setInitialHeight(130);
				modal.show(target);
			}
		});
		add(new Label("name", new Model(file.getName())));

		if (file.isDirectory()) {
			view.setVisible(false);
			edit.setVisible(false);
			clone.setVisible(false);
			remove.setVisible(false);

			add(new Image("icon", iconFolder));
		} else if (file.getName().endsWith("css")) {
			add(new Image("icon", iconCss));
		} else if (file.getName().endsWith("png")) {
			edit.setVisible(false);
			clone.setVisible(false);
			remove.setVisible(false);
			add(new Image("icon", iconPng));
		} else if (file.getName().endsWith("html") || file.getName().endsWith("htm")) {
			add(new Image("icon", iconHtml));
		} else if (file.getName().endsWith("jpg") || file.getName().endsWith("jpeg")) {
			edit.setVisible(false);
			clone.setVisible(false);
			add(new Image("icon", iconJpeg));
		} else if (file.getName().endsWith("js")) {
			add(new Image("icon", iconJs));
		} else {
			view.setVisible(false);
			add(new Image("icon", iconUnknown));
		}
		clone.setVisible(false);
	}

}
