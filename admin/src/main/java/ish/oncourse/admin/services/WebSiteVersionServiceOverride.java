/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.admin.services;

import ish.oncourse.model.WebContent;
import ish.oncourse.model.WebSiteLayout;
import ish.oncourse.model.WebSiteVersion;
import ish.oncourse.services.site.IWebSiteVersionService;
import org.apache.cayenne.ObjectContext;

public class WebSiteVersionServiceOverride implements IWebSiteVersionService {

	@Override
	public WebSiteVersion getCurrentVersion() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void deploy() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void deleteWebSiteVersion(WebSiteVersion versionToDelete) {
		ObjectContext context = versionToDelete.getObjectContext();

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
