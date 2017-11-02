/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.services.resource;

import ish.oncourse.model.WebSiteLayout;
import ish.oncourse.model.WebSiteVersion;
import ish.oncourse.model.WebTemplate;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.site.IWebSiteVersionService;
import org.apache.cayenne.query.ObjectSelect;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class WebTemplateChangeTracker {
	
	private ICayenneService cayenneService;
	private IWebSiteVersionService webSiteVersionService;
	private IWebSiteService webSiteService;
	
	private Map<String, Long> lastCheckTimestamp = new HashMap<>();

	public WebTemplateChangeTracker(ICayenneService cayenneService, IWebSiteService webSiteService, IWebSiteVersionService webSiteVersionService) {
		this.cayenneService = cayenneService;
		this.webSiteService = webSiteService;
		this.webSiteVersionService = webSiteVersionService;
	}
	
	public void resetTimestamp(String cacheKey) {
		lastCheckTimestamp.put(cacheKey, System.currentTimeMillis());
	}

	public Date getTimestamp(String cacheKey) {
		Long timestamp = lastCheckTimestamp.get(cacheKey);
		if (timestamp == null) {
			timestamp = System.currentTimeMillis();
			lastCheckTimestamp.put(cacheKey, timestamp);
		}
		return new Date(timestamp);
	}
	
	public boolean containsChanges(String cacheKey) {
		//if the requested site is not exist - return false
		if (webSiteService.getCurrentWebSite() == null || cacheKey == null) {
			return false;
		}
		WebSiteVersion webSiteVersion = webSiteVersionService.getCurrentVersion();
		return (ObjectSelect.query(WebTemplate.class)
				.localCache(WebTemplate.class.getSimpleName())
				.and(WebTemplate.LAYOUT.dot(WebSiteLayout.WEB_SITE_VERSION).eq(webSiteVersion))
				.and(WebTemplate.MODIFIED.gt(getTimestamp(cacheKey)))
				.limit(1)
				.selectFirst(cayenneService.sharedContext()) != null);
	}

}
