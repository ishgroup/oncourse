/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.services.site;

import ish.oncourse.model.WebSite;
import ish.oncourse.model.WebSiteVersion;

public interface IWebSiteVersionService {

	/**
	 * Returns currently active version of a website.
	 */
	WebSiteVersion getCurrentVersion(WebSite webSite);

	/**
	 * Makes currently staged website version active by setting its deployedOn field to current time.
	 */
	void deploy(WebSite webSite);
}
