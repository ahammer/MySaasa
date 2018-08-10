package com.mysaasa.core.media.panels.uploader;

import com.mysaasa.MySaasa;
import com.mysaasa.core.media.model.Media;
import com.mysaasa.core.media.services.MediaService;
import com.mysaasa.core.media.messages.MediaUpdatedMessage;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.extensions.ajax.markup.html.form.upload.UploadProgressBar;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class Uploader extends Panel {

	public Uploader(String id) {
		super(id, new Model(""));
		add(new FileUploadForm());
	}

	private class FileUploadForm extends Form {

		private final FileUploadField file;
		private final FeedbackPanel feedback;

		public FileUploadForm() {
			super("FileUploadForm");
			add(file = new FileUploadField("files", new Model("")));
			add(new UploadProgressBar("progress", this, file));
			add(feedback = new FeedbackPanel("feedback"));
			feedback.setOutputMarkupId(true);

			add(new AjaxButton("ajaxSubmit") {
				@Override
				protected void onSubmit(final AjaxRequestTarget target, Form<?> form) {
					info("Success");
					List<Media> uploads = new ArrayList<Media>();
					for (FileUpload fu : file.getFileUploads()) {
						info(fu.getClientFileName() + " Size " + fu.getSize());
						try {
							Media m = new Media(fu);
							MediaService service = MediaService.get();
							m = service.saveMedia(m);
							uploads.add(m);
						} catch (IOException e) {
							error(e.getMessage());
						}
					}
					send(MySaasa.getInstance(), Broadcast.BREADTH, new MediaUpdatedMessage() {

						@Override
						public AjaxRequestTarget getAjaxRequestTarget() {
							return target;
						}
					});
					done(target, uploads);
				}

				@Override
				protected void onError(AjaxRequestTarget target, Form<?> form) {
					// update feedback to display errors
					target.add(feedback);
				}

			});
		}
	}

	public abstract void done(AjaxRequestTarget target, List<Media> uploads);
}
