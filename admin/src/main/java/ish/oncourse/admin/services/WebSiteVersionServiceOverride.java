/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.admin.services;

import ish.oncourse.model.WebContent;
import ish.oncourse.model.WebSiteLayout;
import ish.oncourse.model.WebSiteVersion;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteVersionService;
import org.apache.cayenne.ObjectContext;
import org.apache.tapestry5.ioc.annotations.Inject;

public class WebSiteVersionServiceOverride implements IWebSiteVersionService {

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
}
