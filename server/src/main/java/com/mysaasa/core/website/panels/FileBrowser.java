package com.mysaasa.core.website.panels;

import java.io.File;

import com.mysaasa.core.website.model.Website;
import com.mysaasa.core.help.panels.HelpPanel;
import com.mysaasa.core.security.services.session.AdminSession;
import com.mysaasa.core.website.model.TemplateFile;
import com.mysaasa.core.website.panels.files.NewFile;
import com.mysaasa.messages.ACTIONS;
import com.mysaasa.messages.AjaxIntent;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.extensions.markup.html.repeater.tree.NestedTree;
import org.apache.wicket.extensions.markup.html.repeater.tree.theme.WindowsTheme;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

/**
 * This handles file browsing There is a Tree-layout that shows the browsing.
 */
public abstract class FileBrowser extends Panel {
	private static final long serialVersionUID = 4128140560372545323L;
	private NestedTree<File> tree;

	public FileBrowser(IModel<Website> model) {
		super("fileBrowser", model);
		setOutputMarkupId(true);
		setOutputMarkupPlaceholderTag(true);
		addTree(model);
		add(new HelpPanel("help", HelpPanel.Sections.FileBrowser));
		add(new NewFile("newFile", model) {

			@Override
			public void SuccessfullyCreatedFile(AjaxRequestTarget target, TemplateFile file) {
				target.add(FileBrowser.this);
			}
		});
	}

	private void addTree(final IModel<Website> model) {
		add(tree = new WebsiteFileTree(model));
		tree.add(new ApplyThemeBehaviour());
	}

	@Override
	public void onEvent(IEvent event) {
		if (event.getPayload() instanceof AjaxIntent) {
			AjaxIntent intent = (AjaxIntent) event.getPayload();
			if (intent.getAction().equals(ACTIONS.ACTION_USER_PREFS_UPDATED)
					|| intent.getAction().equalsIgnoreCase(ACTIONS.ACTION_WEBSITE_FILE_CREATED)) {
				intent.getAjaxRequestTarget().add(this);
				remove("tree");
				addTree((IModel<Website>) getDefaultModel());
			}
		}
	}

	/**
	 * We need a default theme for the tree, this code applies the theme
	 */
	private static class ApplyThemeBehaviour extends Behavior {
		private static final WindowsTheme theme = new WindowsTheme();
		private static final long serialVersionUID = 1L;

		@Override
		public void onComponentTag(Component component, ComponentTag tag) {
			theme.onComponentTag(component, tag);
		}

		@Override
		public void renderHead(Component component, IHeaderResponse response) {
			theme.renderHead(component, response);
		}
	}

	/**
	 * Set's up the File Tree based on the correct place
	 */
	private class WebsiteFileTree extends NestedTree<File> {
		private static final long serialVersionUID = 1L;

		public WebsiteFileTree(IModel<Website> model) {
			super("tree", new FileTreeProvider(
					(AdminSession.get().getEnv() != AdminSession.Environment.Staging)
							? model.getObject().calculateProductionRoot()
							: model.getObject().calculateStagingRoot()));
		}

		@Override
		protected Component newContentComponent(String id, IModel<File> model) {
			return new FileTreeItem(id, model, (IModel<Website>) FileBrowser.this.getDefaultModel());
		}
	}

	protected abstract void clickFile(AjaxRequestTarget target, File file);
}
