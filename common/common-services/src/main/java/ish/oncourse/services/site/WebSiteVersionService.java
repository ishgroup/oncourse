/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.services.site;

import ish.oncourse.model.WebSite;
import ish.oncourse.model.WebSiteVersion;
import org.apache.tapestry5.ioc.annotations.Inject;

/**
 * Generic web implemetation of {@link ish.oncourse.services.site.IWebSiteVersionService}. 
 * Current version is determined as the latest deployed website version.
 */
public class WebSiteVersionService extends AbstractWebSiteVersionService {
    @Inject
    private IWebSiteService webSiteService;


	@Override
	public WebSiteVersion getCurrentVersion() {
		return getDeployedVersion(webSiteService.getCurrentWebSite());
	}

	@Override
	public void publish() {
		throw new UnsupportedOperationException("Websites can only be deployed from CMS.");
	}

	@Override
	public void delete(WebSiteVersion versionToDelete) {
		throw new UnsupportedOperationException("WebSiteVersions can only be deleted from CMS.");
	}

	@Override
	public void removeOldWebSiteVersions(WebSite webSite) {
		throw new UnsupportedOperationException("WebSiteVersions can only be deleted from CMS.");
	}
}
