package com.mysaasa.core.media.panels;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.panel.Panel;

public class Header extends Panel {
	private static final long serialVersionUID = 2047188623448222572L;

	public Header(String id) {
		super(id);
		add(new AjaxLink("Upload") {
			@Override
			public void onClick(final AjaxRequestTarget ajaxRequestTarget) {
				throw new RuntimeException("Uploading files, this is a edge case, how we do without dialog?");
				/* send(MySaasa.getInstance(), Broadcast.BREADTH, new CreateModalWindow() {
				 * 
				 * @Override public void initialize(final ModalWindow window) { window.setTitle("Upload Files"); window.setInitialWidth(300); window.setAutoSize(true); window.setContent(new Uploader(window.getContentId()) {
				 * 
				 * @Override public void done(AjaxRequestTarget target, List<Media> uploads) { window.close(target); } }); window.show(getAjaxRequestTarget());
				 * 
				 * }
				 * 
				 * @Override public AjaxRequestTarget getAjaxRequestTarget() { return ajaxRequestTarget; } }); */
			}
		});
	}
}
