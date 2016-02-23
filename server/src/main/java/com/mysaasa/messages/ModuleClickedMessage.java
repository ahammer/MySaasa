package com.mysaasa.messages;

import com.mysaasa.core.AbstractModule;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.IEventSource;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import static com.google.common.base.Preconditions.checkNotNull;

public class ModuleClickedMessage extends AjaxIntent {

	private final AbstractModule module;
	private final IModel model;

	//Should not allow null models in the future
	public ModuleClickedMessage(AbstractModule module, AjaxRequestTarget target) {
		super(target);
		setAction(ACTIONS.ACTION_MODULE_CLICKED);
		this.module = module;
		model = module.getDefaultModel();

	}

	//Should not allow null models in the future
	public ModuleClickedMessage(AbstractModule module, IEventSource source) {
		super(source);
		setAction(ACTIONS.ACTION_MODULE_CLICKED);
		this.module = module;
		model = module.getDefaultModel();

	}

	public ModuleClickedMessage(AbstractModule websiteModule, IModel model, AjaxRequestTarget target) {
		super(target);
		checkNotNull(target, "Use the other Constructor for Page() is AjaxRequestTarget not available");
		setAction(ACTIONS.ACTION_MODULE_CLICKED);
		module = websiteModule;
		this.model = model;
	}

	public ModuleClickedMessage(AbstractModule websiteModule, Model model, IEventSource page) {
		super(page);
		setAction(ACTIONS.ACTION_MODULE_CLICKED);
		module = websiteModule;
		this.model = model;

	}

	public AbstractModule getModule() {
		return module;
	}

	public IModel getModel() {
		return model;
	}

}
