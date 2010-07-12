package ish.oncourse.services.site;

import ish.oncourse.model.College;
import ish.oncourse.model.CollegeDomain;
import java.util.List;

import ish.oncourse.model.WebBlock;
import ish.oncourse.model.WebSite;

public interface IWebSiteService {

	List<WebSite> getAvailableSites();

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
	CollegeDomain getCurrentDomain();

	/**
	 * Retrieve the resource folder name.
	 *
	 * @return resource folder name
	 */
	String getResourceFolderName();

	/**
	 * Get WebBlock matching the name and linked to the current site.
	 *
	 * @param name the name of the block
	 *
	 * @return
	 */
	WebBlock getWebBlockForName(String name);
	
	String getHomeLink();
}
