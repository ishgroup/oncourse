/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.services.site;

import ish.oncourse.model.WebSite;
import ish.oncourse.model.WebSiteVersion;

/**
 * Generic web implemetation of {@link ish.oncourse.services.site.IWebSiteVersionService}. 
 * Current version is determined as the latest deployed website version.
 */
public class WebSiteVersionService extends AbstractWebSiteVersionService {

	@Override
	public WebSiteVersion getCurrentVersion(WebSite webSite) {
		return getDeployedVersion(webSite);
	}

	@Override
	public void deploy(WebSite webSite) {
		throw new UnsupportedOperationException("Websites can only be deployed from CMS.");
	}
}
