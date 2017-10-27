/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.services.site;

import ish.oncourse.model.WebSite;
import ish.oncourse.model.WebSiteVersion;
import ish.oncourse.services.cookies.ICookiesService;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.tapestry5.ioc.annotations.Inject;

/**
 * Generic web implemetation of {@link ish.oncourse.services.site.IWebSiteVersionService}. 
 * Current version is determined as the latest deployed website version.
 */
public class WebSiteVersionService extends AbstractWebSiteVersionService {
    @Inject
    private IWebSiteService webSiteService;
    
	@Inject
	private ICookiesService cookiesService;
	
	private static final String EDITOR_COOKIE_NAME = "editor";

	@Override
	public WebSiteVersion getCurrentVersion() {
		WebSite webSite = webSiteService.getCurrentWebSite();
		return isEditor() ? getDraftVersion(webSite) : getDeployedVersion(webSite);
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

	@Override
	public boolean isEditor() {
		return cookiesService.getCookieValue(EDITOR_COOKIE_NAME) != null;

	}

	private WebSiteVersion getDraftVersion(WebSite webSite) {
		return ObjectSelect.query(WebSiteVersion.class).
				localCache(WebSiteVersion.class.getSimpleName()).
				where(WebSiteVersion.WEB_SITE.eq(webSite)).
				and(WebSiteVersion.DEPLOYED_ON.isNull()).
				selectOne(webSite.getObjectContext());
	}
}
