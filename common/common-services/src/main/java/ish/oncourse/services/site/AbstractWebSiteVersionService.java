/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.services.site;

import ish.oncourse.model.WebSite;
import ish.oncourse.model.WebSiteVersion;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.QueryCacheStrategy;
import org.apache.cayenne.query.SelectQuery;
import org.apache.cayenne.query.SortOrder;

public abstract class AbstractWebSiteVersionService implements IWebSiteVersionService {
	
	public WebSiteVersion getDeployedVersion(WebSite webSite) {
		SelectQuery query = new SelectQuery(WebSiteVersion.class);

		query.andQualifier(ExpressionFactory.matchExp(WebSiteVersion.WEB_SITE_PROPERTY, webSite));
		query.addOrdering(WebSiteVersion.DEPLOYED_ON_PROPERTY, SortOrder.DESCENDING);
		query.setFetchLimit(1);
		
		query.setCacheStrategy(QueryCacheStrategy.SHARED_CACHE);

		return (WebSiteVersion) Cayenne.objectForQuery(webSite.getObjectContext(), query);	
	}
}
