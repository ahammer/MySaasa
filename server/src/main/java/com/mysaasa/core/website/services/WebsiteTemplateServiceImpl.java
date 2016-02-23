package com.mysaasa.core.website.services;

import com.mysaasa.SimpleImpl;
import com.mysaasa.core.website.model.Website;
import com.mysaasa.core.website.templating.TemplateHelperService;
import com.mysaasa.interfaces.annotations.SimpleService;
import com.mysaasa.interfaces.templating.WebsiteTemplateService;
import com.mysaasa.Simple;
import com.mysaasa.core.security.services.SecurityService;
import com.mysaasa.core.website.model.ContentBinding;

/**
 * Created by Adam on 3/15/14.
 */
@SimpleService
public class WebsiteTemplateServiceImpl extends WebsiteTemplateService {
	public WebsiteTemplateServiceImpl() {}

	//  Looks up the text bound to this content tag. If DebugMode is enabled
	//  The content will be wrapped in a Span and made editable.
	//  @Params The name of this ContentBinding
	//  @Returns the String Content RunContext, hiding behind the Content Binding.
	@Override
	public String bind(String name, String defaultValue) {
		WebsiteService service = SimpleImpl.get().getInjector().getProvider(WebsiteService.class).get();
		TemplateHelperService templateHelperService = SimpleImpl.get().getInjector().getProvider(TemplateHelperService.class).get();
		TemplateHelperService.RequestProperties rp = templateHelperService.getRequestProperties();
		Website website = rp.website;
		boolean debugMode = rp.debugMode;

		ContentBinding b = service.findBinding(name, website, defaultValue);
		if (debugMode) {
			return "<div style=\"position:relative;display:inline-block;\">" + "<label style=\"position:absolute;left:-1em;top:-1em;font-size:14px;color:yellow;opacity:0.8;cursor:pointer;\" " + "onClick=\"window.parent.postMessage(" + "{" + "   title:'" + name + "'," + "   method:'delete'," + "   content: $('#content_" + name + "').html()," + "   id: '" + name + "'" + "},'*');\">X</label>" + "<div " + "class=\"EditableContent\" " + "id=\"content_" + name + "\" " + "onClick=\"EditContent('content_" + name + "');\"\"" + " onBlur=\"window.parent.postMessage(" + "{" + "   title:'" + name + "'," + "   method:'save'," + "   content: $('#content_" + name + "').html()," + "   id: '" + name + "'" + "},'*');\">" + b.getContent().getBody() + "</div></div>";
		} else {
			return b.getContent().getBody();
		}
	}

	@Override
	public String bind(String name) {
		return bind(name, name);
	}

	@Override
	public String getAdminLink() {
		String nonce = SecurityService.get().generateNonce();
		if (!nonce.equals("")) {
			return Simple.getBaseDomain() + ":" + Simple.getPort() + "?nonce=" + nonce;
		}
		return Simple.getBaseDomain() + ":" + Simple.getPort();
	}
}
