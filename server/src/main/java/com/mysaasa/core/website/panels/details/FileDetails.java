package com.mysaasa.core.website.panels.details;

import com.mysaasa.core.media.model.Media;
import com.mysaasa.core.website.model.TemplateFile;
import com.mysaasa.core.website.model.Website;
import com.mysaasa.messages.ACTIONS;
import com.mysaasa.messages.AjaxIntent;
import com.mysaasa.messages.EditContentMessage;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import java.io.File;
import java.io.IOException;

/**
 * Created by administrator on 2014-04-30.
 */
public class FileDetails extends Panel {
	private String copyFilename = "";

	public String getCopyFilename() {
		return copyFilename;
	}

	public void setCopyFilename(String copyFilename) {
		this.copyFilename = copyFilename;
	}

	private final IModel<Website> websiteModel;

	public String getFilePath() {
		String websitePath = websiteModel.getObject().calculateWebsiteRootAsString();
		String templatePath = websiteModel.getObject().calculateStagingRootAsString();
		try {
			return getDefaultModelObjectAsString()
					.replace(websitePath.substring(0, websitePath.length() - 1), "")
					.replace(templatePath.substring(0, websitePath.length() - 1), "").substring(1);
		} catch (Exception e) {
			return websitePath;
		}
	}

	@Override
	public void onConfigure() {
		super.onConfigure();
		if (websiteModel == null || websiteModel.getObject() == null) {
			return;
		}
		try {
			Media.Format format = Media.Format.fromFile((File) getDefaultModelObject());
			switch (format) {
			case JPEG:
			case GIF:
			case PNG:
				break;
			default:
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}

	public FileDetails(String id, final CompoundPropertyModel<File> fileCompoundPropertyModel, final IModel<Website> website) {
		super(id, fileCompoundPropertyModel);
		this.websiteModel = website;

		if (fileCompoundPropertyModel.getObject() == null) {
			fileCompoundPropertyModel.setObject(new File(website.getObject().calculateProductionRoot().getPath() + "/index.html"));
		}

		add(new Label("path", new PropertyModel(this, "filePath")));

		setOutputMarkupId(true);
		setOutputMarkupPlaceholderTag(true);
	}

	@Override
	public void onEvent(IEvent event) {
		if (event.getPayload() instanceof EditContentMessage) {
			final EditContentMessage wfc = (EditContentMessage) event.getPayload();
			if (wfc.getModel().getObject() instanceof TemplateFile)
				setDefaultModel(new CompoundPropertyModel<TemplateFile>((TemplateFile) wfc.getModel().getObject()));
			wfc.getAjaxRequestTarget().add(this);
		} else if (event.getPayload() instanceof AjaxIntent) {
			AjaxIntent intent = (AjaxIntent) event.getPayload();
			if (intent.getAction().equals(ACTIONS.ACTION_WEBSITE_IFRAME_PAGELOAD)) {
				FileDetails.this.setDefaultModel(new CompoundPropertyModel<File>(new TemplateFile(intent.getExtras().getString("file"))));
				intent.getAjaxRequestTarget().add(FileDetails.this);
			}
		}

	}

	public String getMime() {
		try {
			return Media.Format.fromFile((File) getDefaultModelObject()).getMimeType();
		} catch (IOException e) {

			return "Error: " + e.getLocalizedMessage();
		} catch (Exception e) {
			return "Unknown";
		}
	}
}
