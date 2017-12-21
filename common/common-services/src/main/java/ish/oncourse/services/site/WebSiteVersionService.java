/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.services.site;

import ish.oncourse.cayenne.cache.ICacheEnabledService;
import ish.oncourse.model.WebSite;
import ish.oncourse.model.WebSiteVersion;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.QueryCacheStrategy;
import org.apache.tapestry5.ioc.annotations.Inject;

/**
 * Generic web implemetation of {@link ish.oncourse.services.site.IWebSiteVersionService}. 
 * Current version is determined as the latest deployed website version.
 */
public class WebSiteVersionService extends AbstractWebSiteVersionService {
    @Inject
    private IWebSiteService webSiteService;
	
	@Inject
	private ICacheEnabledService enabledService;
	
	public static final String EDITOR_PREFIX = "editor";
	
	private static final String WEB_PREFIX = "web";


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
		return !enabledService.isCacheEnabled();
	}

	@Override
	public String getApplicationKey() {
		WebSite webSite = webSiteService.getCurrentWebSite();
		if (webSite != null) {
			String prefix = isEditor() ? EDITOR_PREFIX : WEB_PREFIX;
			return String.format("%s-%s", prefix, webSite.getSiteKey());
		}
		return null;
	}

	private WebSiteVersion getDraftVersion(WebSite webSite) {
		return ObjectSelect.query(WebSiteVersion.class)
				.cacheStrategy(QueryCacheStrategy.LOCAL_CACHE, WebSiteVersion.class.getSimpleName()).
				where(WebSiteVersion.WEB_SITE.eq(webSite)).
				and(WebSiteVersion.DEPLOYED_ON.isNull()).
				selectOne(webSite.getObjectContext());
	}
}
