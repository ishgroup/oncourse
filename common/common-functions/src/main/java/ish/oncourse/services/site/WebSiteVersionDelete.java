package ish.oncourse.services.site;

import ish.oncourse.model.WebSiteVersion;
import org.apache.cayenne.ObjectContext;


/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class WebSiteVersionDelete {

    private WebSiteVersion current;
    private WebSiteVersion deployed;
	private WebSiteVersion deleting;

	private ObjectContext context;

	public void delete() {
		DeleteVersion.valueOf(deleting).delete();
		context.commitChanges();
	}

    private void validate() {
        if (current.getId().equals(deleting.getId())
                || deployed.getId().equals(deleting.getId())) {
            // prevent the deletion of the current live site or the draft site!
            throw new IllegalArgumentException("Attempt to delete current live site or the draft site version");
        }
    }

    public static WebSiteVersionDelete valueOf(WebSiteVersion deleting,
                                               WebSiteVersion current,
                                               WebSiteVersion deployed,
                                               ObjectContext object) {
        WebSiteVersionDelete result = new WebSiteVersionDelete();
        result.deleting = object.localObject(deleting);
        result.current = object.localObject(current);
        result.deployed = object.localObject(deployed);
		result.context = object;

        result.validate();
        return result;
    }
}
