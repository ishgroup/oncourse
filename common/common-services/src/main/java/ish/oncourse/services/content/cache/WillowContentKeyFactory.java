/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.services.content.cache;

import ish.oncourse.model.WebSite;
import ish.oncourse.model.WebSiteLayout;
import ish.oncourse.services.node.IWebNodeService;
import ish.oncourse.services.site.IWebSiteService;
import org.apache.cayenne.PersistentObject;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

/**
 * User: akoiro
 * Date: 8/09/2016
 */
public class WillowContentKeyFactory implements IContentKeyFactory<PersistentObject, WillowContentKey> {

	@Inject
	private Request request;

	@Inject
	private IWebSiteService webSiteService;

	@Inject
	private IWebNodeService webNodeService;

	@Override
	public WillowContentKey createKey(String tapestryElement, PersistentObject relatedObject) {
		WebSite site = webSiteService.getCurrentWebSite();
		WebSiteLayout layout = webNodeService.getLayout();

		return WillowContentKey.valueOf(site, layout, relatedObject, tapestryElement);
	}
}
