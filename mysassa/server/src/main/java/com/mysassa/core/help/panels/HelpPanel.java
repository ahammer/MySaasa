package com.mysassa.core.help.panels;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

/**
 * Created by adam on 15-02-11.
 */
public class HelpPanel extends Panel {
	private final ModalWindow modalWindow;

	public static enum Sections {
		SplashSignin(
				"Signin",
				"<div class=\"help_section\"><p>Use this form to login.</p><p> If you have forgotten your password, enter your Identifier or Email and then click Recover Password</p></div>"), FileBrowser(
						"File Browser",
						"<div class=\"help_section\"><p> The File browser allows you to view your website files. </p><p> The current environment (staging/production) is defined in the title</p>" + "<p>If the file allows you can do the following</p>" + "<ul>" + "<li> Code Edit </li>" + "<li> Web Edit </li>" + "<li> File Deletion </li>" + "</ul></p>" + "<p> You can also upload files by typing in a name, and using the upload form </p>" + "</p>" + "</div>"), WebsiteSidebar(
								"WebsiteEditor",
								"<div class=\"help_section\">" + "<p>Website Editor, edit your Blogs and Static Content. Choose a file from the bottom file Browser and Edit on the right</p></div>"),

		;

		public final String details;
		public final String title;

		Sections(String title, String details) {
			this.title = title;
			this.details = details;
		}
	};

	public HelpPanel(String id, final Sections section) {
		super(id, new Model(section));
		add(modalWindow = new ModalWindow("modal"));
		add(new AjaxLink("launch") {
			@Override
			public void onClick(AjaxRequestTarget target) {
				modalWindow.setTitle("Help: " + section.title);
				modalWindow.setContent(new Label(modalWindow.getContentId(), section.details).setEscapeModelStrings(false));
				modalWindow.setInitialWidth(600);
				modalWindow.setAutoSize(true);
				modalWindow.show(target);
			}
		});
	}

}
