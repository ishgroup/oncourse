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
import org.apache.cayenne.query.QueryCacheStrategy;

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

	public void resetTimestamp() {
		lastCheckTimestamp.put(webSiteVersionService.getApplicationKey(), System.currentTimeMillis());
	}

	private Date getTimestamp(String applicationKey) {
		Long timestamp = lastCheckTimestamp.get(applicationKey);
		if (timestamp == null) {
			timestamp = System.currentTimeMillis();
			lastCheckTimestamp.put(applicationKey, timestamp);
		}
		return new Date(timestamp);
	}

	public boolean containsChanges() {
		String applicationKey = webSiteVersionService.getApplicationKey();
		if (webSiteService.getCurrentWebSite() == null || applicationKey == null) {
			return false;
		}
		WebSiteVersion webSiteVersion = webSiteVersionService.getCurrentVersion();
		return (ObjectSelect.query(WebTemplate.class)
				.cacheStrategy(QueryCacheStrategy.LOCAL_CACHE, WebTemplate.class.getSimpleName())
				.and(WebTemplate.LAYOUT.dot(WebSiteLayout.WEB_SITE_VERSION).eq(webSiteVersion))
				.and(WebTemplate.MODIFIED.gt(getTimestamp(applicationKey)))
				.limit(1)
				.selectFirst(cayenneService.sharedContext()) != null);
	}

}
