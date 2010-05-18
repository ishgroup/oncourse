package ish.oncourse.ui.components;

import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.ioc.annotations.Inject;

/**
 * A page wrapper component.
 */
public class PageWrapper {

	@Inject
	private ComponentResources componentResources;

	public String getBodyId() {
		// TODO: andrus, Nov 14, 2009 - there's more to it than this, see Willow
		// PageWrapper
		return componentResources.getPageName();
	}
}
