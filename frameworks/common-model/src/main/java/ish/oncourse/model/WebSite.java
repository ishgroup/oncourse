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

	/**
	 * The site resource folder identifier is constructed from
	 * {@link WebSite#getSiteKey()} properties.
	 *
	 * @return site resources folder location.
	 */
	public String getSiteIdentifier() {
		return getSiteKey();
	}

}
