/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.services.site;

import ish.oncourse.model.WebSite;
import ish.oncourse.model.WebSiteVersion;
import ish.oncourse.services.cache.RequestCached;

public interface IWebSiteVersionService {

	/**
	 * Returns currently active version of a website.
	 */
    @RequestCached
	WebSiteVersion getCurrentVersion();

	/**
	 * Create a copy of the staged web site version, sets deployedOn field to current time
	 */
	void publish();

	/**
	 * deletes the specified WebSiteVersion with all objects related to it.
	 */
	void delete(WebSiteVersion version);

	/**
	 * delete all revisions older than 60 days, but always to keep at least 5 revisions, even if they are older
	 */
	void removeOldWebSiteVersions(WebSite webSite);
}
