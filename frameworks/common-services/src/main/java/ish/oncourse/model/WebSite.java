package ish.oncourse.model;

import ish.oncourse.model.auto._WebSite;

public class WebSite extends _WebSite {

	public boolean hasParentSite() {
		return getParentSite() != null;
	}

	/**
	 * Returns site resources folder location.
	 */
	public String getSiteIdentifier() {
		WebSite parent = getParentSite();
		if (parent != null && parent.getCode() != null) {
			return parent.getCode() + "_" + getCode();
		}
		return getCode();
	}

}
