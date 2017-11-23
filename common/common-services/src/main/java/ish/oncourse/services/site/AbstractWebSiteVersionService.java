/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.services.site;

import ish.oncourse.model.WebSite;
import ish.oncourse.model.WebSiteVersion;
import ish.oncourse.services.persistence.ICayenneService;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.QueryCacheStrategy;
import org.apache.tapestry5.ioc.annotations.Inject;

public abstract class AbstractWebSiteVersionService implements IWebSiteVersionService {

    @Inject
    private ICayenneService cayenneService;

	public WebSiteVersion getDeployedVersion(WebSite webSite) {
        return GetDeployedVersion.valueOf(cayenneService.sharedContext(), webSite, true).get();
	}

    public boolean isEditor() {
	    return false;
    }
}
