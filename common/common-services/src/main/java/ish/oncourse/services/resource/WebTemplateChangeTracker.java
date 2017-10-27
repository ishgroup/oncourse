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
	
	private long webLastCheckTimestamp;
	private long editorLastCheckTimestamp;


	public WebTemplateChangeTracker(ICayenneService cayenneService, IWebSiteService webSiteService, IWebSiteVersionService webSiteVersionService) {
		this.cayenneService = cayenneService;
		this.webSiteService = webSiteService;
		this.webSiteVersionService = webSiteVersionService;
		
		this.webLastCheckTimestamp = System.currentTimeMillis();
		this.editorLastCheckTimestamp = System.currentTimeMillis();
	}
	
	public void resetEditorTimestamp() {
		this.editorLastCheckTimestamp = System.currentTimeMillis();
	}

	public void resetWebTimestamp() {
		this.webLastCheckTimestamp = System.currentTimeMillis();
	}

	public boolean containsChanges() {
		//if the requested site is not exist - return false
		if (webSiteService.getCurrentWebSite() == null) {
			return false;
		}
		WebSiteVersion webSiteVersion = webSiteVersionService.getCurrentVersion();
		return (ObjectSelect.query(WebTemplate.class)
				.localCache(WebTemplate.class.getSimpleName())
				.and(WebTemplate.LAYOUT.dot(WebSiteLayout.WEB_SITE_VERSION).eq(webSiteVersion))
				.and(WebTemplate.MODIFIED.gt(new Date(webSiteVersionService.isEditor() ? editorLastCheckTimestamp : webLastCheckTimestamp)))
				.limit(1)
				.selectFirst(cayenneService.sharedContext()) != null);
	}

}
