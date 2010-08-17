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
	 * @return true if this WebSite has a parent WebSite, false otherwise
	 */
	public boolean hasParentWebSite() {
		return getParentWebSite() != null;
	}

	/**
	 * The site resource folder identifier is constructed from
	 * {@link College#getCollegeKey()} and the optional
	 * {@link WebSite#getSiteKey()} properties.
	 *
	 * <p><b>Important!</b> We are currently not taking care of the case where a WebSite
	 * has a parent WebSite record.</p>
	 *
	 * @return site resources folder location.
	 */
	public String getSiteIdentifier() {
		String identifier = getCollege().getCollegeKey();

		//FIXME: Take care of the parent site case.
		if ((getSiteKey() != null) && !("".equals(getSiteKey()))) {
			identifier += "_" + getSiteKey();
		}

		return identifier;
	}

}
