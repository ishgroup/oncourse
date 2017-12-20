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
		WebSiteLayout webSiteLayout = webNodeService.getLayout();
		if (ctd != null && name.endsWith(ctd.getTemplateClassName()))
			return new MultiKey(name, ctd.getTemplateFileName(), selector.locale, request.getServerName(), webSiteLayout != null ? webSiteLayout.getId(): null);
		else
			return new MultiKey(name, selector.locale, request.getServerName(), webSiteLayout != null ? webSiteLayout.getId(): null);
	}
}
