package ish.oncourse.services.site;

import ish.oncourse.model.College;
import ish.oncourse.model.WebHostName;
import ish.oncourse.model.WebSite;
import ish.oncourse.services.cache.RequestCached;

import java.util.TimeZone;


public interface IWebSiteService {

	/**
	 * Get the web site for the requested domain.
	 *
	 * @return current WebSite
	 */
    @RequestCached
	WebSite getCurrentWebSite();

	/**
	 * Get the college for the requested domain.
	 *
	 * @return current College
	 */
    @RequestCached
	College getCurrentCollege();


	@RequestCached
	TimeZone getTimezone();
}
