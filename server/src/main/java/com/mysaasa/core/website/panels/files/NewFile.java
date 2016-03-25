package com.mysaasa.core.website.panels.files;

import com.mysaasa.core.website.model.Website;
import com.mysaasa.core.website.model.TemplateFile;
import org.apache.commons.io.IOUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;

/**
 * Created by administrator on 2014-05-04.
 */
public abstract class NewFile extends Panel {

	public abstract void SuccessfullyCreatedFile(AjaxRequestTarget target, TemplateFile file);

	public NewFile(String id, IModel<Website> model) {
		super(id, model);
		add(new NewFileForm("form"));
	}

	public static class NewFileFormData implements Serializable {
		public NewFileFormData() {
			this.filename = "";
		}

		private String filename;

		public String getFilename() {
			return filename;
		}

		public void setFilename(String filename) {
			this.filename = filename;
		}
	}

	public class NewFileForm extends Form {
		private final FeedbackPanel feedback;
		private final FileUploadField file;

		public NewFileForm(String id) {
			super(id, new CompoundPropertyModel(new NewFileFormData()));
			setOutputMarkupId(true);
			add(file = new FileUploadField("file", new Model("")));
			add(new TextField<String>("filename").add(new FilenameValidator()));
			add(new AjaxSubmitLink("submit") {

				@Override
				protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
					super.onSubmit(target, form);

					Website website = (Website) NewFile.this.getDefaultModelObject();
					NewFileFormData data = (NewFileFormData) NewFileForm.this.getDefaultModelObject();
					TemplateFile f = (data.getFilename() != null && !data.getFilename().trim().equals("")) ? new TemplateFile(website.calculateWebsiteRootAsString() + data.getFilename(), website) : new TemplateFile(website.calculateWebsiteRootAsString() + file.getFileUpload().getClientFileName(), website);

					if (file.getFileUploads().size() == 1) {
						try {
							IOUtils.copy(file.getFileUpload().getInputStream(), new FileOutputStream(f));
						} catch (IOException e) {
							e.printStackTrace();
						}
					}

					SuccessfullyCreatedFile(target, f);
				}

				@Override
				protected void onError(AjaxRequestTarget target, Form<?> form) {
					super.onError(target, form);
					error("Error");
					target.add(feedback);
				}
			});
			add(feedback = new FeedbackPanel("feedback"));
			feedback.setOutputMarkupId(true);
		}

		private class FilenameValidator implements IValidator<String> {

			@Override
			public void validate(IValidatable<String> validatable) {
				String value = validatable.getValue();

				Website website = (Website) NewFile.this.getDefaultModelObject();
				File f = new File(website.calculateWebsiteRootAsString() + value);
				if (value.contains("..")) {
					error("Double dot not allowed");
					return;
				}
				if (f.exists()) {
					error("File already exists");
					return;
				}
				try {
					if (!f.createNewFile())
						error("Could not create file");
				} catch (IOException e) {
					error("Io Exception: " + e.getLocalizedMessage());
				}
			}
		}
	}
}
