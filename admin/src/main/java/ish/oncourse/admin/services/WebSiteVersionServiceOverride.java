/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.admin.services;

import ish.oncourse.model.WebSite;
import ish.oncourse.model.WebSiteVersion;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.site.IWebSiteVersionService;
import org.apache.cayenne.query.ObjectSelect;
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

	@Override
	public WebSiteVersion getDeployedVersion(WebSite webSite) {
		return ObjectSelect.query(WebSiteVersion.class)
				.and(WebSiteVersion.WEB_SITE.eq(webSite))
				.orderBy(WebSiteVersion.DEPLOYED_ON.desc())
				.limit(1).selectFirst(webSite.getObjectContext());
	}

	@Override
	public String getCacheKey() {
		throw new UnsupportedOperationException("Unsupported operation for admin application");
	}
}
