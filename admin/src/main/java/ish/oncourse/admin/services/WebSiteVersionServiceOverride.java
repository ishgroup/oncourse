/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.admin.services;

import ish.oncourse.model.WebSite;
import ish.oncourse.model.WebSiteVersion;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.site.IWebSiteVersionService;
import org.apache.tapestry5.ioc.annotations.Inject;

public class WebSiteVersionServiceOverride implements IWebSiteVersionService {
	
	@Inject
	private IWebSiteService webSiteService;

	@Inject
	private ICayenneService cayenneService;

	@Override
	public WebSiteVersion getCurrentVersion() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void publish() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void delete(WebSiteVersion webSiteVersionToDelte) {
		throw new UnsupportedOperationException("WebSiteVersions can only be deleted from CMS.");
	}
	 
	@Override
	public void removeOldWebSiteVersions(WebSite webSite) {
		throw new UnsupportedOperationException("WebSiteVersions can only be deleted from CMS.");
	}
}
