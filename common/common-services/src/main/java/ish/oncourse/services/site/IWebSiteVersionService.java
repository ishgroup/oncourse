/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.services.site;

import ish.oncourse.model.WebSiteVersion;
import ish.oncourse.services.cache.RequestCached;

public interface IWebSiteVersionService {

	/**
	 * Returns currently active version of a website.
	 */
    @RequestCached
	WebSiteVersion getCurrentVersion();

	/**
	 * Makes currently staged website version active by setting its deployedOn field to current time.
	 */
	void deploy();
}
