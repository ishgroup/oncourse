/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.ui.services.pageload.template;

import ish.oncourse.model.WebSiteLayout;
import ish.oncourse.services.node.IWebNodeService;
import ish.oncourse.services.textile.CustomTemplateDefinition;
import org.apache.tapestry5.internal.util.MultiKey;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.pageload.ComponentResourceSelector;

import java.util.Locale;

/**
 * User: akoiro
 * Date: 22/11/17
 */
public class GetTemplateKey {

	private final GetTemplateDefinition getTemplateDefinition;
	private final Request request;
	private final IWebNodeService webNodeService;

	public GetTemplateKey(IWebNodeService webNodeService, Request request) {
		getTemplateDefinition = new GetTemplateDefinition(request);
		this.webNodeService = webNodeService;
		this.request = request;

	}

	public MultiKey get(String name, ComponentResourceSelector selector) {
		CustomTemplateDefinition ctd = getTemplateDefinition.get(name);
		if (ctd != null && name.endsWith(ctd.getTemplateClassName()))
			return new MultiKey(name, ctd.getTemplateFileName(), selector.locale, request.getServerName(), webNodeService.getLayout());
		else
			return new MultiKey(name, selector.locale, request.getServerName(), webNodeService.getLayout());
	}

	public static MultiKey multiKey(String name, String templateFileName, Locale locale, String serverName, WebSiteLayout layout) {
		return new MultiKey(name, templateFileName, locale, serverName, layout);
	}

	public static MultiKey multiKey(String name, Locale locale, String serverName, WebSiteLayout layout) {
		return new MultiKey(name, locale, serverName, layout);
	}


}
