/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.ui.services.pageload.template;

import ish.oncourse.model.WebSiteLayout;
import ish.oncourse.services.node.IWebNodeService;
import org.apache.tapestry5.internal.util.MultiKey;
import org.apache.tapestry5.services.pageload.ComponentResourceSelector;

/**
 * User: akoiro
 * Date: 22/11/17
 */
public class GetTemplateKey {

	private final IWebNodeService webNodeService;

	public GetTemplateKey(IWebNodeService webNodeService) {
		this.webNodeService = webNodeService;
	}

	public MultiKey get(String name, CacheKey cacheKey, ComponentResourceSelector selector) {
		WebSiteLayout webSiteLayout = webNodeService.getLayout();
		return new MultiKey(name, cacheKey, selector, webSiteLayout != null ? webSiteLayout.getId() : null);
	}
}
