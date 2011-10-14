package ish.oncourse.model;

import ish.oncourse.model.auto._WebSite;

/**
 * The WebSite entity represents an instance of a single web site. A
 * {@link College} may have several web sites, each with its own navigation and
 * pages.
 * 
 * @author Various
 */
public class WebSite extends _WebSite {

	private static final String DEFAULT_FOLDER_NAME = "default";

	/**
	 * The site resource folder identifier is constructed from
	 * {@link WebSite#getSiteKey()} properties.
	 * 
	 * @return site resources folder location.
	 */
	public String getSiteIdentifier() {
		return getSiteKey();
	}
	
	
	/**
	 * Retrieve the resource folder name.
	 * 
	 * <p>
	 * The resource folder name is the root folder containing site specific
	 * resources. The name is constructed from the Site key.
	 * </p>
	 * 
	 * <p>
	 * Defaults to the "defaults" folder.
	 * </p>
	 * 
	 * @return resource folder name
	 */
	public String getResourceFolderName() {
		String siteKey = getSiteKey();
		return ((siteKey != null) && !("".equals(siteKey))) ? siteKey : DEFAULT_FOLDER_NAME;
	}
}
