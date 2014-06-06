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
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.QueryCacheStrategy;
import org.apache.cayenne.query.SelectQuery;

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
		WebSiteVersion webSiteVersion = webSiteVersionService.getCurrentVersion(webSiteService.getCurrentWebSite());
		
		SelectQuery query = new SelectQuery(WebTemplate.class);
		
		query.andQualifier(ExpressionFactory.matchExp(WebTemplate.LAYOUT_PROPERTY + "." + WebSiteLayout.WEB_SITE_VERSION_PROPERTY, webSiteVersion));
		query.andQualifier(ExpressionFactory.greaterExp(WebTemplate.MODIFIED_PROPERTY, new Date(lastCheckTimestamp)));
		query.setFetchLimit(1);
		
		query.setCacheStrategy(QueryCacheStrategy.LOCAL_CACHE);
		
		return !cayenneService.sharedContext().performQuery(query).isEmpty();
	}

}
