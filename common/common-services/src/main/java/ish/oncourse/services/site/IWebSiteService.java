package ish.oncourse.services.site;

import ish.oncourse.model.College;
import ish.oncourse.model.WebHostName;
import ish.oncourse.model.WebSite;


public interface IWebSiteService {

	/**
	 * Get the web site for the requested domain.
	 *
	 * @return current WebSite
	 */
	WebSite getCurrentWebSite();

	/**
	 * Get the college for the requested domain.
	 *
	 * @return current College
	 */
	College getCurrentCollege();

	/**
	 * Lookup the CollegeDomain for current request.
	 *
	 * @return domain
	 */
	WebHostName getCurrentDomain();
}
