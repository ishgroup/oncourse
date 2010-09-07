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
		// FIXME: MSW 7-Sep-2010 The Body ID logic needs to be more complex.
		// Suggestion - use Entity name + _ + entityID
		// For pages which are more generic or don't represent a single entity do ???
		return componentResources.getPageName();
	}
}
