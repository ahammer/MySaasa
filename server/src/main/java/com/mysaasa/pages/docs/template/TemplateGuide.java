package com.mysaasa.pages.docs.template;

import com.mysaasa.core.website.templating.TemplateHelperService;
import com.mysaasa.interfaces.ITemplateService;
import com.mysaasa.Simple;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

/**
 * Created by Adam on 3/12/14.
 */
public class TemplateGuide extends WebPage {
	public TemplateGuide() {
		final TemplateHelperService service = Simple.getInstance().getInjector().getInstance(TemplateHelperService.class);
		ListView repeater;
		Set<String> keys = service.getServiceMap().keySet();
		final List<String> list = new ArrayList();
		list.addAll(keys);

		add(repeater = new ListView("TemplateCalls", Model.of((Collection) list)) {
			@Override
			protected void populateItem(ListItem item) {
				Class c = service.getServiceMap().get(item.getModelObject());
				ITemplateService template = (ITemplateService) Simple.getInstance().getInjector().getInstance(c);
				item.add(new Label("divider", new Model(template.getTemplateInterfaceName())));
				Method[] methods = c.getDeclaredMethods();
				List<Method> methodList = Arrays.asList(methods);
				item.add(new ListView("entries", Model.of((Collection) methodList)) {
					@Override
					protected void populateItem(ListItem item) {
						Method method = (Method) item.getModelObject();
						List<Parameter> params = Arrays.asList(method.getParameters());
						String paramString = "(";
						if (params.size() > 0) {
							for (Parameter param : params) {
								paramString += param.getType().getSimpleName() + " <span class='bg-info'>" + param.getName() + "</span>,";
							}
							paramString = paramString.substring(0, paramString.length() - 1) + ")";
						} else {
							paramString += ")";
						}

						item.add(new Label("entry", new Model(method.getName() + paramString)).setEscapeModelStrings(false));
						item.add(new Label("returns", method.getReturnType().getSimpleName()));

					}
				});
			}
		});
	};

}
