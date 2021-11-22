/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.services.site;

import ish.oncourse.model.Invoice;
import ish.oncourse.model.WebSite;
import ish.oncourse.model.WebSiteVersion;
import org.apache.cayenne.ObjectContext;

import java.util.LinkedList;
import java.util.List;

public class WebSiteDelete {

	private WebSite site;
	private ObjectContext context;

	private WebSiteDelete() {}

	public static WebSiteDelete valueOf(WebSite site, ObjectContext objectContext) {
		WebSiteDelete instance = new WebSiteDelete();
		instance.site = site;
		instance.context = objectContext;
		return instance;
	}
	
	
	public void delete() {
		
		List<WebSiteVersion> versions = new LinkedList<>(site.getVersions());
				
		for (WebSiteVersion version : versions) {
			DeleteVersion.valueOf(version, context).delete();
		}
		
		List<Invoice> invoices = new LinkedList<>(site.getInvoices());
		for (Invoice invoice : invoices) {
			invoice.setWebSite(null);
		}
		context.deleteObjects(site.getCollegeDomains());
		context.deleteObject(site.getPreferences());
		context.deleteObject(site);
		context.commitChanges();
	}

}
