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

import static ish.oncourse.cayenne.cache.ICacheEnabledService.CacheDisableReason.EDITOR;

/**
 * Generic web implementation of {@link ish.oncourse.services.site.IWebSiteVersionService}.
 * Current version is determined as the latest deployed website version.
 */
public class WebSiteVersionService extends AbstractWebSiteVersionService {
    @Inject
    private IWebSiteService webSiteService;
	
	@Inject
	private ICacheEnabledService cacheEnabledService;
	
	public static final String EDITOR_PREFIX = "editor";
	
	public static final String WEB_PREFIX = "web";


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

	/**
	 * TODO: Use special separate cookies to resolve cache mode and editor mode
	 * It is not correct to define editor mode by checking whether enable cache or not.
	 *
	 * @return true if cache is disabled and reason to disable cache is EDITOR mode
	 */
	@Override
	public boolean isEditor() {
		return !cacheEnabledService.isCacheEnabled() && (cacheEnabledService.getDisableReason() == EDITOR);
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
				.cacheStrategy(QueryCacheStrategy.LOCAL_CACHE)
				.cacheGroup(webSite.getSiteKey())
				.where(WebSiteVersion.WEB_SITE.eq(webSite))
				.and(WebSiteVersion.DEPLOYED_ON.isNull())
				.selectOne(webSite.getObjectContext());
	}
}
