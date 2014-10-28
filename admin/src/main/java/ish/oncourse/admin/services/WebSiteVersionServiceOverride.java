/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.admin.services;

import ish.oncourse.model.WebContent;
import ish.oncourse.model.WebSite;
import ish.oncourse.model.WebSiteLayout;
import ish.oncourse.model.WebSiteVersion;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.site.IWebSiteVersionService;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.cayenne.query.SortOrder;
import org.apache.commons.lang.time.DateUtils;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.*;

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
	public void deploy() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void deleteWebSiteVersion(WebSiteVersion webSiteVersionToDelte) {

		ObjectContext context = cayenneService.newContext();
		WebSiteVersion versionToDelete = context.localObject(webSiteVersionToDelte);

		for (WebSiteLayout layoutToDelete : versionToDelete.getLayouts()) {
			context.deleteObjects(layoutToDelete.getTemplates());
		}
		context.deleteObjects(versionToDelete.getLayouts());

		for (WebContent contentToDelete : versionToDelete.getContents()) {
			context.deleteObjects(contentToDelete.getWebContentVisibilities());
		}
		context.deleteObjects(versionToDelete.getContents());

		context.deleteObjects(versionToDelete.getMenus());

		context.deleteObjects(versionToDelete.getWebURLAliases());
		context.deleteObjects(versionToDelete.getWebNodes());
		context.deleteObjects(versionToDelete.getWebNodeTypes());

		context.deleteObjects(versionToDelete);

		context.commitChanges();
	}
	
	
	//delete all revisions older than 60 days, but always to keep at least 5 revisions, even if they are older
	 
	@Override
	public void removeOldWebSiteVersions(WebSite webSite) {

		ObjectContext context = cayenneService.newContext();

		Date date = DateUtils.addDays(new Date(), -60);
		
		
		SelectQuery query = new SelectQuery(WebSiteVersion.class);
		query.andQualifier(ExpressionFactory.matchExp(WebSiteVersion.WEB_SITE_PROPERTY, webSite));
		//exclude unpublished revisions yet
		query.andQualifier(ExpressionFactory.noMatchExp(WebSiteVersion.DEPLOYED_ON_PROPERTY, null));
		query.addOrdering(WebSiteVersion.DEPLOYED_ON_PROPERTY, SortOrder.DESCENDING);
		
		List<WebSiteVersion> allVersions = context.performQuery(query);
		
		//if number of revisions less than 5 (4 + 1 unpublished) - nothing to delete 
		if (allVersions.size() > 4) {
			List<WebSiteVersion> versionsToDelete = new ArrayList<>();
					
			List<WebSiteVersion> lastVersions = ExpressionFactory.greaterExp(WebSiteVersion.DEPLOYED_ON_PROPERTY, date).filterObjects(allVersions);
			if (lastVersions.size() >= 4) {
				//if number of revisions which younger than 60 days more than 5 - simply delete all revisions older than 60 days 
				versionsToDelete.addAll(ExpressionFactory.lessExp(WebSiteVersion.DEPLOYED_ON_PROPERTY, date).filterObjects(allVersions));
			} else {
				//if number of revisions which younger than 60 days less than 5 then keep at least 5 revisions, even if they are older
				versionsToDelete.addAll(allVersions.subList(4, allVersions.size()));
			}
			
			for (WebSiteVersion version : versionsToDelete) {
				deleteWebSiteVersion(version);
			}
		}
	}
}
