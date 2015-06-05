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

public class WebTemplateChangeTracker {
	
	private ICayenneService cayenneService;
	private IWebSiteVersionService webSiteVersionService;
	private IWebSiteService webSiteService;
	
	private long lastCheckTimestamp;
	
	public WebTemplateChangeTracker(ICayenneService cayenneService, IWebSiteService webSiteService, IWebSiteVersionService webSiteVersionService) {
		this.cayenneService = cayenneService;
		this.webSiteService = webSiteService;
		this.webSiteVersionService = webSiteVersionService;
		
		this.lastCheckTimestamp = System.currentTimeMillis();
	}

	public void resetTimestamp() {
		this.lastCheckTimestamp = System.currentTimeMillis();
	}

	public boolean containsChanges() {
		//if the requested site is not exist - return false
		if (webSiteService.getCurrentWebSite() == null) {
			return false;
		}
		WebSiteVersion webSiteVersion = webSiteVersionService.getCurrentVersion();
		return (ObjectSelect.query(WebTemplate.class)
				.and(WebTemplate.LAYOUT.dot(WebSiteLayout.WEB_SITE_VERSION).eq(webSiteVersion))
				.and(WebTemplate.MODIFIED.gt(new Date(lastCheckTimestamp)))
				.limit(1)
				.cacheGroups(WebTemplate.class.getSimpleName())
				.cacheStrategy(QueryCacheStrategy.LOCAL_CACHE).selectFirst(cayenneService.sharedContext()) != null);
	}

}
