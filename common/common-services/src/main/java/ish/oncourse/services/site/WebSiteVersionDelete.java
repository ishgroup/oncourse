package ish.oncourse.services.site;

import ish.oncourse.model.WebSiteVersion;
import org.apache.cayenne.ObjectContext;


/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class WebSiteVersionDelete extends AbstractWebSiteVersionDelete {

    private WebSiteVersion currentVersion;
    private WebSiteVersion deployedVersion;
	protected ObjectContext objectContext;
	protected WebSiteVersion deletingVersion;
	
	public void delete() {
		deleteVersion(deletingVersion, objectContext);
		objectContext.commitChanges();
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
