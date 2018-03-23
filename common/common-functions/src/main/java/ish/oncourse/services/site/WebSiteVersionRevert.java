/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.services.site;


import ish.oncourse.model.WebSiteVersion;
import org.apache.cayenne.ObjectContext;

public class WebSiteVersionRevert extends WebSiteVersionCopy {
    
    public static WebSiteVersionRevert valueOf(WebSiteVersion draftVersion, WebSiteVersion fromVersion, ObjectContext context) {
        WebSiteVersionRevert reverter = new WebSiteVersionRevert();
        reverter.toVersion = draftVersion;
        reverter.fromVersion = fromVersion;
        reverter.context = context;
        return reverter;
    }

    public void revert() {
        DeleteVersion.valueOf(toVersion, context, true).delete();
        toVersion.setSiteVersion(fromVersion.getSiteVersion());
        fromVersion.setSiteVersion(GetNextSiteVersion.valueOf(context, fromVersion.getWebSite()).get());
        copyContent();
        context.commitChanges();
    }
    
}
