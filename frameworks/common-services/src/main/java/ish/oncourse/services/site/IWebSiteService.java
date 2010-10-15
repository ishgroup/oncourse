package ish.oncourse.services.site;

import java.util.List;

import ish.oncourse.model.College;
import ish.oncourse.model.WebHostName;
import ish.oncourse.model.Site;
import ish.oncourse.model.WebContent;
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
	WebHostName getCurrentDomain();

	/**
	 * Retrieve the resource folder name.
	 *
	 * @return resource folder name
	 */
	String getResourceFolderName();

	/**
	 * Get WebContent matching the regionKey and linked to the current site.
	 *
	 * @param regionKey - the region
	 *
	 * @return list matching of web blocks
	 */
	List<WebContent> getWebBlocksForRegion(String regionKey);

	/**
	 * Get WebContent matching the name and linked to the current site.
	 *
	 * @param name the name of the block
	 *
	 * @return
	 */
	WebContent getWebBlockForName(String name);

	String getHomeLink();
	
	List<Site> getCollegeSites();
	
	boolean isCollegePaymentEnabled();
}
