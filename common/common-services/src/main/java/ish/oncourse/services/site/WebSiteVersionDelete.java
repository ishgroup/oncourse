package ish.oncourse.services.site;

import ish.oncourse.model.WebContent;
import ish.oncourse.model.WebMenu;
import ish.oncourse.model.WebSiteLayout;
import ish.oncourse.model.WebSiteVersion;
import org.apache.cayenne.ObjectContext;

import java.util.ArrayList;
import java.util.List;

/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class WebSiteVersionDelete {

    private WebSiteVersion deletingVersion;

    private WebSiteVersion currentVersion;
    private WebSiteVersion deployedVersion;

    private ObjectContext objectContext;

    public void delete() {

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

            deleteChildrenMenus(rootMenu.getChildrenMenus());
            objectContext.deleteObject(rootMenu);
        }

        objectContext.deleteObjects(deletingVersion.getMenus());

        objectContext.deleteObjects(deletingVersion.getWebURLAliases());
        objectContext.deleteObjects(deletingVersion.getWebNodes());
        objectContext.deleteObjects(deletingVersion.getWebNodeTypes());
		objectContext.deleteObjects(deletingVersion.getLayouts());
        objectContext.deleteObjects(deletingVersion.getContents());

        objectContext.deleteObjects(deletingVersion);

        objectContext.commitChanges();


    }

    //recursively remove all childrenMenus then remove parent
    private void deleteChildrenMenus(List<WebMenu> webMenus) {
        List<WebMenu> copyList = new ArrayList<>(webMenus);
        for (WebMenu webMenu : copyList) {
            if (!webMenu.getChildrenMenus().isEmpty()) {
                deleteChildrenMenus(webMenu.getChildrenMenus());
            }
            objectContext.deleteObject(webMenu);
        }
    }

    private void validate() {
        if (currentVersion.getId().equals(deletingVersion.getId())
                || deployedVersion.getId().equals(deletingVersion.getId())) {
            // prevent the deletion of the current live site or the draft site!
            throw new IllegalArgumentException("Attempt to delete current live site or the draft site version");
        }
    }

    public static WebSiteVersionDelete valueOf(WebSiteVersion deletingVersion,
                                               WebSiteVersion currentVersion,
                                               WebSiteVersion deployedVersion,
                                               ObjectContext objectContext) {
        WebSiteVersionDelete result = new WebSiteVersionDelete();
        result.deletingVersion = objectContext.localObject(deletingVersion);
        result.currentVersion = objectContext.localObject(currentVersion);
        result.deployedVersion = objectContext.localObject(deployedVersion);
        result.objectContext = objectContext;

        result.validate();
        return result;
    }
}
