/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.admin.services;

import ish.oncourse.model.WebSite;
import ish.oncourse.model.WebSiteVersion;
import ish.oncourse.services.site.IWebSiteVersionService;

public class WebSiteVersionServiceOverride implements IWebSiteVersionService {

	@Override
	public WebSiteVersion getCurrentVersion(WebSite webSite) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void deploy(WebSite webSite) {
		throw new UnsupportedOperationException();
	}
}
