/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.services.site;

import ish.oncourse.model.*;
import org.apache.cayenne.ObjectContext;

public class DeleteVersion {
	private ObjectContext context;
	private WebSiteVersion version;
	private boolean deleteRelatedObjectsOnly;

	public void delete() {
		
		deleteAllWebTemplates(context, version);
		deleteAllWebContents(context, version);

		context.deleteObjects(version.getMenus());
		context.deleteObjects(version.getWebURLAliases());
		context.deleteObjects(version.getWebNodes());
		context.deleteObjects(version.getWebLayoutPaths());
		context.deleteObjects(version.getWebNodeTypes());
		context.deleteObjects(version.getLayouts());
		
		if (!deleteRelatedObjectsOnly) {
			context.invalidateObjects(version);
			context.deleteObject(version);        // delete version record and related with ON CASCADE DELETE;
		}

		context.commitChanges();
	}

	private void deleteAllWebContents(ObjectContext context, WebSiteVersion version) {
		for (WebContent content : version.getContents()) {
			context.deleteObjects(content.getWebContentVisibilities());
		}
		context.deleteObjects(version.getContents());
	}

	private void deleteAllWebTemplates(ObjectContext context, WebSiteVersion version) {
		for (WebSiteLayout layout : version.getLayouts()) {
			context.deleteObjects(layout.getTemplates());
		}
	}

	public static DeleteVersion valueOf(WebSiteVersion version, ObjectContext context) {
		return valueOf(version, context, false);
	}

	public static DeleteVersion valueOf(WebSiteVersion version) {
		return valueOf(version, version.getObjectContext(), false);
	}

	public static DeleteVersion valueOf(WebSiteVersion version, ObjectContext context, boolean deleteRelatedObjectsOnly) {
		DeleteVersion deleteVersion = new DeleteVersion();
		deleteVersion.version = version;
		deleteVersion.context = context;
		deleteVersion.deleteRelatedObjectsOnly = deleteRelatedObjectsOnly;
		return deleteVersion;
	}
}
