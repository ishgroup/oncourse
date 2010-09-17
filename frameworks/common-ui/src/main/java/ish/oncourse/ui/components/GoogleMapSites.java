package ish.oncourse.ui.components;

import ish.oncourse.model.Site;

import java.util.Collection;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;

public class GoogleMapSites {

	@Parameter(required = true)
	@Property
	private Collection<Site> sites;

	@Parameter
	@Property
	private boolean collapsed;

	public boolean isHasMapItemList() {
		return !sites.isEmpty();
	}

	public String getFocusMapClass() {
		return collapsed ? "collapse" : "non-collapse";
	}
}
