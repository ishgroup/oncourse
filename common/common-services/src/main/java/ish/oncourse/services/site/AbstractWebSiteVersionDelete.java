/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.services.site;

import ish.oncourse.model.WebContent;
import ish.oncourse.model.WebMenu;
import ish.oncourse.model.WebSiteLayout;
import ish.oncourse.model.WebSiteVersion;
import org.apache.cayenne.ObjectContext;

import java.util.ArrayList;
import java.util.List;

public abstract  class AbstractWebSiteVersionDelete {

	public void deleteVersion(WebSiteVersion deletingVersion, ObjectContext objectContext) {

		for (WebSiteLayout layoutToDelete : deletingVersion.getLayouts()) {
			objectContext.deleteObjects(layoutToDelete.getTemplates());
		}

		for (WebContent contentToDelete : deletingVersion.getContents()) {
			objectContext.deleteObjects(contentToDelete.getWebContentVisibilities());
		}
		objectContext.deleteObjects(deletingVersion.getContents());


		//find root menu for entry in menus tree
		if (!deletingVersion.getMenus().isEmpty()) {
			WebMenu rootMenu = deletingVersion.getMenus().get(0);
			while (rootMenu.getParentWebMenu() != null) {
				rootMenu = rootMenu.getParentWebMenu();
			}

			deleteChildrenMenus(rootMenu.getChildrenMenus(), objectContext);
			objectContext.deleteObject(rootMenu);
		}

		objectContext.deleteObjects(deletingVersion.getMenus());

		objectContext.deleteObjects(deletingVersion.getWebURLAliases());
		objectContext.deleteObjects(deletingVersion.getWebNodes());
		objectContext.deleteObjects(deletingVersion.getWebNodeTypes());
		objectContext.deleteObjects(deletingVersion.getLayouts());
		objectContext.deleteObjects(deletingVersion.getContents());

		objectContext.deleteObjects(deletingVersion);
	}

	//recursively remove all childrenMenus then remove parent
	private void deleteChildrenMenus(List<WebMenu> webMenus, ObjectContext objectContext) {
		List<WebMenu> copyList = new ArrayList<>(webMenus);
		for (WebMenu webMenu : copyList) {
			if (!webMenu.getChildrenMenus().isEmpty()) {
				deleteChildrenMenus(webMenu.getChildrenMenus(), objectContext);
			}
			objectContext.deleteObject(webMenu);
		}
	}

}
