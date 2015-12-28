package com.mysassa.simple.pages.docs.api;

import com.mysassa.simple.Simple;
import com.mysassa.simple.api.ApiHelperService;
import com.mysassa.simple.api.ApiMapping;
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
public class ApiGuide extends WebPage {

	public ApiGuide() {
		final ApiHelperService service = Simple.get().getInjector().getInstance(ApiHelperService.class);
		ListView repeater;
		String[] keys = service.getPaths();

		final List<String> list = Arrays.asList(keys);
		list.sort((String s1, String s2) -> s1.compareTo(s2));

		add(repeater = new ListView("ApiCalls", Model.of((Collection) list)) {
			@Override
			protected void populateItem(ListItem item) {
				ApiMapping c = service.getMapping((String) item.getModelObject());

				item.add(new Label("divider", item.getModel()));
				Method[] methods = new Method[]{c.getMethod()};
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
