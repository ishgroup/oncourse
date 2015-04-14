package ish.oncourse.ui.components;

import ish.oncourse.model.Site;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;

import java.util.Collection;
import java.util.Map;

public class GoogleMapSites {

	@Parameter(required = true)
	@Property
	private Collection<Site> sites;
	
	@Parameter
	@Property
	private Map<Integer, Float> focuses;
	
	@Parameter
	@Property
	private boolean collapsed;

	@Parameter
	@Property
	private boolean showLocationMap;
	
	public boolean isHasMapItemList() {
		return !sites.isEmpty();
	}

	public String getFocusMapClass() {
		return collapsed ? "collapse" : "non-collapse";
	}
}
