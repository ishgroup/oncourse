/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.ui.services.pageload.template;

import ish.oncourse.model.WebSiteLayout;
import ish.oncourse.services.node.IWebNodeService;
import org.apache.tapestry5.internal.util.MultiKey;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.pageload.ComponentResourceSelector;

/**
 * User: akoiro
 * Date: 22/11/17
 */
public class GetTemplateKey {

	private final Request request;
	private final IWebNodeService webNodeService;

	public GetTemplateKey(IWebNodeService webNodeService, Request request) {
		this.webNodeService = webNodeService;
		this.request = request;
	}

	public MultiKey get(String name, ComponentResourceSelector selector) {
		WebSiteLayout webSiteLayout = webNodeService.getLayout();
		return new MultiKey(name, selector, request.getServerName(), webSiteLayout != null ? webSiteLayout.getId() : null);
	}
}
